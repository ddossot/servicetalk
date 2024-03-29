/*
 * Copyright © 2018 Apple Inc. and the ServiceTalk project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicetalk.http.netty;

import io.servicetalk.concurrent.api.Executor;
import io.servicetalk.concurrent.api.Executors;
import io.servicetalk.concurrent.api.Publisher;
import io.servicetalk.concurrent.internal.DefaultThreadFactory;
import io.servicetalk.concurrent.internal.ServiceTalkTestTimeout;
import io.servicetalk.http.api.HttpPayloadChunk;
import io.servicetalk.http.api.HttpPayloadChunks;
import io.servicetalk.http.api.HttpProtocolVersions;
import io.servicetalk.http.api.HttpResponseStatuses;
import io.servicetalk.http.api.StreamingHttpConnection;
import io.servicetalk.http.api.StreamingHttpRequest;
import io.servicetalk.http.api.StreamingHttpResponse;
import io.servicetalk.http.api.StreamingHttpService;
import io.servicetalk.test.resources.DefaultTestCerts;
import io.servicetalk.transport.api.ContextFilter;
import io.servicetalk.transport.api.DefaultExecutionContext;
import io.servicetalk.transport.api.IoExecutor;
import io.servicetalk.transport.api.ServerContext;
import io.servicetalk.transport.api.SslConfig;
import io.servicetalk.transport.api.SslConfigBuilder;
import io.servicetalk.transport.netty.IoThreadFactory;
import io.servicetalk.transport.netty.NettyIoExecutors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.servicetalk.buffer.netty.BufferAllocators.DEFAULT_ALLOCATOR;
import static io.servicetalk.concurrent.api.AsyncCloseables.newCompositeCloseable;
import static io.servicetalk.concurrent.api.Executors.newCachedThreadExecutor;
import static io.servicetalk.concurrent.internal.Await.await;
import static io.servicetalk.concurrent.internal.Await.awaitIndefinitely;
import static io.servicetalk.concurrent.internal.Await.awaitIndefinitelyNonNull;
import static io.servicetalk.transport.api.ContextFilter.ACCEPT_ALL;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Thread.NORM_PRIORITY;
import static java.net.InetAddress.getLoopbackAddress;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;

public abstract class AbstractNettyHttpServerTest {

    enum ExecutorSupplier {
        IMMEDIATE(Executors::immediate),
        CACHED(() -> newCachedThreadExecutor(new DefaultThreadFactory("client-executor", true, NORM_PRIORITY)));

        final Supplier<Executor> executorSupplier;

        ExecutorSupplier(final Supplier<Executor> executorSupplier) {
            this.executorSupplier = executorSupplier;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNettyHttpServerTest.class);
    private static final InetAddress LOOPBACK_ADDRESS = getLoopbackAddress();

    @Rule
    public final ServiceTalkTestTimeout timeout = new ServiceTalkTestTimeout();
    @Rule
    public final MockitoRule rule = MockitoJUnit.rule().silent();

    @Mock
    Function<StreamingHttpRequest<HttpPayloadChunk>, Publisher<HttpPayloadChunk>> publisherSupplier;

    private static IoExecutor clientIoExecutor;
    private static IoExecutor serverIoExecutor;

    private final Executor clientExecutor;
    private final Executor serverExecutor;
    private final ExecutorSupplier clientExecutorSupplier;
    private final ExecutorSupplier serverExecutorSupplier;
    private ContextFilter contextFilter = ACCEPT_ALL;
    private boolean sslEnabled;
    private ServerContext serverContext;
    private StreamingHttpConnection httpConnection;
    private StreamingHttpService service;

    AbstractNettyHttpServerTest(ExecutorSupplier clientExecutorSupplier, ExecutorSupplier serverExecutorSupplier) {
        this.clientExecutorSupplier = clientExecutorSupplier;
        this.serverExecutorSupplier = serverExecutorSupplier;
        this.clientExecutor = clientExecutorSupplier.executorSupplier.get();
        this.serverExecutor = serverExecutorSupplier.executorSupplier.get();
    }

    @BeforeClass
    public static void createIoExecutors() {
        clientIoExecutor = NettyIoExecutors.createIoExecutor(new IoThreadFactory("client-io-executor"));
        serverIoExecutor = NettyIoExecutors.createIoExecutor(new IoThreadFactory("server-io-executor"));
    }

    @Before
    public void startServer() throws Exception {
        final InetSocketAddress bindAddress = new InetSocketAddress(LOOPBACK_ADDRESS, 0);
        setService(new TestServiceStreaming(publisherSupplier));

        // A small SNDBUF is needed to test that the server defers closing the connection until writes are complete.
        // However, if it is too small, tests that expect certain chunks of data will see those chunks broken up
        // differently.
        final DefaultHttpServerStarter httpServerStarter = new DefaultHttpServerStarter()
                .setSocketOption(StandardSocketOptions.SO_SNDBUF, 100);
        if (sslEnabled) {
            final SslConfig sslConfig = SslConfigBuilder.forServer(
                    DefaultTestCerts::loadServerPem,
                    DefaultTestCerts::loadServerKey)
                    .build();
            httpServerStarter.setSslConfig(sslConfig);
        }
        serverContext = awaitIndefinitelyNonNull(
                httpServerStarter
                        .start(new DefaultExecutionContext(DEFAULT_ALLOCATOR, serverIoExecutor, serverExecutor),
                                bindAddress, contextFilter, service)
                        .doBeforeSuccess(ctx -> LOGGER.debug("Server started on {}.", ctx.getListenAddress()))
                        .doBeforeError(throwable -> LOGGER.debug("Failed starting server on {}.", bindAddress)));

        final InetSocketAddress socketAddress = new InetSocketAddress(LOOPBACK_ADDRESS,
                ((InetSocketAddress) serverContext.getListenAddress()).getPort());

        final DefaultHttpConnectionBuilder<Object> httpConnectionBuilder = new DefaultHttpConnectionBuilder<>();
        if (sslEnabled) {
            final SslConfig sslConfig = SslConfigBuilder.forClientWithoutServerIdentity()
                    .trustManager(DefaultTestCerts::loadMutualAuthCaPem).build();
            httpConnectionBuilder.setSslConfig(sslConfig);
        }
        httpConnection = awaitIndefinitelyNonNull(httpConnectionBuilder.buildStreaming(
                new DefaultExecutionContext(DEFAULT_ALLOCATOR, clientIoExecutor, clientExecutor), socketAddress));
    }

    protected void ignoreTestWhen(ExecutorSupplier clientExecutorSupplier, ExecutorSupplier serverExecutorSupplier) {
        assumeThat("Ignored flaky test",
                parseBoolean(System.getenv("CI")) &&
                this.clientExecutorSupplier == clientExecutorSupplier &&
                this.serverExecutorSupplier == serverExecutorSupplier, is(FALSE));
    }

    void setService(final StreamingHttpService service) {
        this.service = service;
    }

    @After
    public void stopServer() throws Exception {
        awaitIndefinitely(newCompositeCloseable().appendAll(httpConnection, clientExecutor, serverContext).closeAsync());
    }

    @AfterClass
    public static void shutdownClientIoExecutor() throws Exception {
        awaitIndefinitely(newCompositeCloseable().appendAll(clientIoExecutor, serverIoExecutor).closeAsync());
    }

    void setContextFilter(final ContextFilter contextFilter) {
        this.contextFilter = contextFilter;
    }

    void setSslEnabled(final boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    boolean getSslEnabled() {
        return sslEnabled;
    }

    ServerContext getServerContext() {
        return serverContext;
    }

    Function<StreamingHttpRequest<HttpPayloadChunk>, Publisher<HttpPayloadChunk>> getPublisherSupplier() {
        return publisherSupplier;
    }

    StreamingHttpResponse<HttpPayloadChunk> makeRequest(final StreamingHttpRequest<HttpPayloadChunk> request)
            throws Exception {
        return awaitIndefinitelyNonNull(httpConnection.request(request));
    }

    void assertResponse(final StreamingHttpResponse<HttpPayloadChunk> response, final HttpProtocolVersions version,
                        final HttpResponseStatuses status, final int expectedSize)
            throws ExecutionException, InterruptedException {
        assertEquals(status, response.getStatus());
        assertEquals(version, response.getVersion());

        final int size = awaitIndefinitelyNonNull(
                response.getPayloadBody().reduce(() -> 0, (is, c) -> is + c.getContent().getReadableBytes()));
        assertEquals(expectedSize, size);
    }

    void assertResponse(final StreamingHttpResponse<HttpPayloadChunk> response, final HttpProtocolVersions version,
                        final HttpResponseStatuses status, final List<String> expectedPayloadChunksAsStrings)
            throws ExecutionException, InterruptedException {
        assertEquals(status, response.getStatus());
        assertEquals(version, response.getVersion());
        final List<String> bodyAsListOfStrings = getBodyAsListOfStrings(response);
        assertEquals(expectedPayloadChunksAsStrings, bodyAsListOfStrings);
        assertEquals(expectedPayloadChunksAsStrings.size(), bodyAsListOfStrings.size());
    }

    Publisher<HttpPayloadChunk> getChunkPublisherFromStrings(final String... texts) {
        return Publisher.from(texts).map(this::getChunkFromString);
    }

    HttpPayloadChunk getChunkFromString(final String text) {
        return HttpPayloadChunks.newPayloadChunk(DEFAULT_ALLOCATOR.fromAscii(text));
    }

    static List<String> getBodyAsListOfStrings(final StreamingHttpResponse<HttpPayloadChunk> response)
            throws ExecutionException, InterruptedException {
        return awaitIndefinitelyNonNull(response.getPayloadBody().map(c -> c.getContent().toString(US_ASCII)));
    }

    void assertConnectionClosed() throws Exception {
        await(httpConnection.onClose(), 1000, MILLISECONDS);
    }
}
