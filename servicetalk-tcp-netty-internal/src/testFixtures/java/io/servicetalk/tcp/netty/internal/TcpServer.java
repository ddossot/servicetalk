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
package io.servicetalk.tcp.netty.internal;

import io.servicetalk.buffer.api.Buffer;
import io.servicetalk.concurrent.api.Completable;
import io.servicetalk.concurrent.api.Publisher;
import io.servicetalk.transport.api.ConnectionContext;
import io.servicetalk.transport.api.ContextFilter;
import io.servicetalk.transport.api.ExecutionContext;
import io.servicetalk.transport.api.ServerContext;
import io.servicetalk.transport.netty.internal.AbstractContextFilterAwareChannelReadHandler;
import io.servicetalk.transport.netty.internal.BufferHandler;
import io.servicetalk.transport.netty.internal.ChannelInitializer;
import io.servicetalk.transport.netty.internal.Connection;
import io.servicetalk.transport.netty.internal.NettyConnection;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import javax.annotation.Nullable;

import static io.servicetalk.concurrent.internal.Await.awaitIndefinitelyNonNull;
import static io.servicetalk.transport.api.ContextFilter.ACCEPT_ALL;

/**
 * A utility to create a TCP server for tests.
 */
public class TcpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServer.class);
    private final ReadOnlyTcpServerConfig config;

    /**
     * New instance with default configuration.
     */
    public TcpServer() {
        this(new TcpServerConfig(false));
    }

    /**
     * New instance.
     *
     * @param config for the server.
     */
    public TcpServer(TcpServerConfig config) {
        this.config = config.asReadOnly();
    }

    /**
     * Starts the server at the passed {@code port} and invoke the passed {@code service} for each accepted connection.
     * Awaits for the server to start.
     *
     * @param executionContext {@link ExecutionContext} to use for incoming connections.
     * @param port Port for the server.
     * @param service {@link Function} that is invoked for each accepted connection.
     * @return {@link ServerContext} for the started server.
     * @throws ExecutionException If the server start failed.
     * @throws InterruptedException If the calling thread was interrupted waiting for the server to start.
     */
    public ServerContext start(ExecutionContext executionContext, int port,
                               Function<Connection<Buffer, Buffer>, Completable> service)
            throws ExecutionException, InterruptedException {
        return start(executionContext, port, ACCEPT_ALL, service);
    }

    /**
     * Starts the server at the passed {@code port} and invoke the passed {@code service} for each accepted connection.
     * Awaits for the server to start.
     *
     * @param executionContext {@link ExecutionContext} to use for incoming connections.
     * @param port Port for the server.
     * @param contextFilter to use for filtering accepted connections. The returned {@link ServerContext} manages the
     * lifecycle of the {@code contextFilter}, ensuring it is closed when the {@link ServerContext} is closed.
     * @param service {@link Function} that is invoked for each accepted connection.
     * @return {@link ServerContext} for the started server.
     * @throws ExecutionException If the server start failed.
     * @throws InterruptedException If the calling thread was interrupted waiting for the server to start.
     */
    public ServerContext start(ExecutionContext executionContext, int port, ContextFilter contextFilter,
                               Function<Connection<Buffer, Buffer>, Completable> service)
            throws ExecutionException, InterruptedException {
        TcpServerInitializer initializer = new TcpServerInitializer(executionContext, config);
        return awaitIndefinitelyNonNull(initializer.start(new InetSocketAddress(port), contextFilter,
                new TcpServerChannelInitializer(config, contextFilter)
                        .andThen(getChannelInitializer(service, executionContext)), false, false)
                .doBeforeSuccess(ctx -> LOGGER.info("Server started on port {}.", getServerPort(ctx)))
                .doBeforeError(throwable -> LOGGER.error("Failed starting server on port {}.", port)));
    }

    // Visible to allow tests to override.
    ChannelInitializer getChannelInitializer(final Function<Connection<Buffer, Buffer>, Completable> service,
                                             final ExecutionContext executionContext) {
        return (channel, context) -> {
            channel.pipeline().addLast(new BufferHandler(executionContext.getBufferAllocator()));
            channel.pipeline().addLast(new TcpServerChannelReadHandler(context, service));
            return context;
        };
    }

    /**
     * Returns the listening port for the server represented by {@link ServerContext}.
     *
     * @param context for the server.
     * @return Listening port.
     * @throws ClassCastException If the {@link SocketAddress} returned by {@link ServerContext#getListenAddress()} is
     * not an {@link InetSocketAddress}.
     */
    public static int getServerPort(ServerContext context) {
        return ((InetSocketAddress) context.getListenAddress()).getPort();
    }

    private static class TcpServerChannelReadHandler extends AbstractContextFilterAwareChannelReadHandler<Buffer> {

        private final ConnectionContext context;
        private final Function<Connection<Buffer, Buffer>, Completable> service;
        @Nullable
        private Connection<Buffer, Buffer> conn;

        TcpServerChannelReadHandler(final ConnectionContext context,
                                    final Function<Connection<Buffer, Buffer>, Completable> service) {
            super(buffer -> false);
            this.context = context;
            this.service = service;
        }

        @Override
        protected void onPublisherCreation(ChannelHandlerContext ctx, Publisher<Buffer> newPublisher) {
            conn = new NettyConnection<>(ctx.channel(), context,
                    newPublisher);
        }

        @Override
        protected void onContextFilterSuccess(final ChannelHandlerContext ctx) {
            service.apply(conn)
                    .doBeforeError(throwable -> LOGGER.error("Error handling a connection.", throwable))
                    .doBeforeFinally(() -> ctx.channel().close())
                    .subscribe();
        }
    }
}
