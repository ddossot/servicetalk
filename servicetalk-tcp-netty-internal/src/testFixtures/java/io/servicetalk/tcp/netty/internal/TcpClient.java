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
import io.servicetalk.buffer.api.BufferAllocator;
import io.servicetalk.transport.api.ExecutionContext;
import io.servicetalk.transport.api.FileDescriptorSocketAddress;
import io.servicetalk.transport.netty.internal.BufferHandler;
import io.servicetalk.transport.netty.internal.ChannelInitializer;
import io.servicetalk.transport.netty.internal.Connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.unix.UnixChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.servicetalk.concurrent.internal.Await.awaitIndefinitelyNonNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeTrue;

/**
 * A utility to create a TCP clients for tests.
 */
public final class TcpClient {

    private final ReadOnlyTcpClientConfig config;

    /**
     * New instance with default configuration.
     */
    public TcpClient() {
        this(defaultConfig());
    }

    /**
     * New instance.
     *
     * @param config for the client.
     */
    public TcpClient(TcpClientConfig config) {
        this.config = config.asReadOnly();
    }

    private TcpConnector<Buffer, Buffer> createConnector(BufferAllocator allocator) {
        ChannelInitializer initializer = new TcpClientChannelInitializer(config);
        initializer = initializer.andThen((channel, context) -> {
            channel.pipeline().addLast(new BufferHandler(allocator));
            return context;
        });
        return new TcpConnector<>(config, initializer, () -> buffer -> false);
    }

    /**
     * Connect and await for the connection.
     *
     * @param executionContext {@link ExecutionContext} to use for the connections.
     * @param port to connect.
     * @return New {@link Connection}.
     * @throws ExecutionException If connect failed.
     * @throws InterruptedException If interrupted while waiting for connect to complete.
     */
    public Connection<Buffer, Buffer> connectBlocking(ExecutionContext executionContext, int port)
            throws ExecutionException, InterruptedException {
        return connectBlocking(executionContext, new InetSocketAddress(port));
    }

    /**
     * Connect and await for the connection.
     *
     * @param executionContext {@link ExecutionContext} to use for the connections.
     * @param address to connect.
     * @return New {@link Connection}.
     * @throws ExecutionException If connect failed.
     * @throws InterruptedException If interrupted while waiting for connect to complete.
     */
    public Connection<Buffer, Buffer> connectBlocking(ExecutionContext executionContext, SocketAddress address)
            throws ExecutionException, InterruptedException {
        TcpConnector<Buffer, Buffer> connector = createConnector(executionContext.getBufferAllocator());
        return awaitIndefinitelyNonNull(connector.connect(executionContext, address));
    }

    /**
     * Connect using a {@link FileDescriptorSocketAddress} and await for the connection.
     *
     * @param executionContext {@link ExecutionContext} to use for the connections.
     * @param address to connect.
     * @return New {@link Connection}.
     * @throws ExecutionException If connect failed.
     * @throws InterruptedException If interrupted while waiting for connect to complete.
     */
    public Connection<Buffer, Buffer> connectWithFdBlocking(ExecutionContext executionContext, SocketAddress address)
            throws ExecutionException, InterruptedException {
        assumeTrue(executionContext.getIoExecutor().isFileDescriptorSocketAddressSupported());
        assumeTrue(Epoll.isAvailable() || KQueue.isAvailable());

        final Class<? extends Channel> channelClass;
        final EventLoopGroup eventLoopGroup;
        if (Epoll.isAvailable()) {
            eventLoopGroup = new EpollEventLoopGroup(1);
            channelClass = EpollSocketChannel.class;
        } else {
            eventLoopGroup = new KQueueEventLoopGroup(1);
            channelClass = KQueueSocketChannel.class;
        }
        AtomicBoolean dataReadDirectlyFromNetty = new AtomicBoolean();
        // Bootstrap a netty channel to the server so we can access its FD and wrap it later in ST.
        Bootstrap bs = new Bootstrap();
        UnixChannel channel = (UnixChannel) bs.channel(channelClass)
                .group(eventLoopGroup)
                .handler(new SimpleChannelInboundHandler<Object>() {
                    @Override
                    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
                        dataReadDirectlyFromNetty.set(true);
                    }
                })
                .connect(address)
                .syncUninterruptibly().channel();

        // Unregister it from the netty EventLoop as we want to to handle it via ST.
        channel.deregister().syncUninterruptibly();
        FileDescriptorSocketAddress fd = new FileDescriptorSocketAddress(channel.fd().intValue());
        Connection<Buffer, Buffer> connection = connectBlocking(executionContext, fd);
        assertThat("Data read on the FileDescriptor from netty pipeline.",
                dataReadDirectlyFromNetty.get(), is(false));
        return connection;
    }

    /**
     * Returns configuration for this client.
     *
     * @return {@link ReadOnlyTcpClientConfig} for this client.
     */
    public ReadOnlyTcpClientConfig getConfig() {
        return config;
    }

    private static TcpClientConfig defaultConfig() {
        TcpClientConfig config = new TcpClientConfig(false);
        // To test coverage of options.
        config.setSocketOption(StandardSocketOptions.SO_KEEPALIVE, true);
        return config;
    }
}
