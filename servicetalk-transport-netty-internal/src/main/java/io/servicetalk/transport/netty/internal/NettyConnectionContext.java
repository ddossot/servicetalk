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
package io.servicetalk.transport.netty.internal;

import io.servicetalk.concurrent.api.Completable;
import io.servicetalk.transport.api.ConnectionContext;
import io.servicetalk.transport.api.ExecutionContext;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import javax.annotation.Nullable;
import javax.net.ssl.SSLSession;

import static java.util.Objects.requireNonNull;

/**
 * {@link ConnectionContext} using a netty {@link Channel}.
 */
public final class NettyConnectionContext implements ConnectionContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyConnectionContext.class);

    private final ExecutionContext executionContext;
    private final Channel channel;
    private final NettyChannelListenableAsyncCloseable close;
    @Nullable
    private volatile SSLSession sslSession;

    /**
     * New instance.
     *
     * @param executionContext {@link ExecutionContext} for this connection.
     * @param channel {@link Channel} for this connection.
     */
    private NettyConnectionContext(ExecutionContext executionContext, Channel channel) {
        this.executionContext = requireNonNull(executionContext);
        this.channel = requireNonNull(channel);
        close = new NettyChannelListenableAsyncCloseable(channel, executionContext.getExecutor());
    }

    @Override
    public SocketAddress getLocalAddress() {
        return channel.localAddress();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return channel.remoteAddress();
    }

    @Override
    @Nullable
    public SSLSession getSslSession() {
        return sslSession;
    }

    @Override
    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    /**
     * Creates a new {@link NettyConnectionContext} by initializing the passed {@code channel} using the
     * {@code initializer}.
     *
     * @param executionContext {@link ExecutionContext} for this connection.
     * @param channel for the newly created {@link NettyConnectionContext}.
     * @param initializer to initialize the channel.
     * @return New {@link ConnectionContext} for the channel.
     */
    public static ConnectionContext newContext(ExecutionContext executionContext, Channel channel,
                                               ChannelInitializer initializer) {
        return newContext(executionContext, channel, initializer, true);
    }

    /**
     * Creates a new {@link NettyConnectionContext} by initializing the passed {@code channel} using the
     * {@code initializer}.
     *
     * @param executionContext {@link ExecutionContext} for this connection.
     * @param channel for the newly created {@link NettyConnectionContext}.
     * @param initializer to initialize the channel.
     * @param checkForRefCountedTrapper Whether to log a warning if a {@link RefCountedTrapper} is not found in the
     * pipeline.
     * @return New {@link ConnectionContext} for the channel.
     */
    public static ConnectionContext newContext(ExecutionContext executionContext, Channel channel,
                                               ChannelInitializer initializer, boolean checkForRefCountedTrapper) {
        ConnectionContext context = new NettyConnectionContext(executionContext, channel);
        context = initializer.init(channel, context);
        if (checkForRefCountedTrapper) {
            RefCountedTrapper refCountedTrapper = channel.pipeline().get(RefCountedTrapper.class);
            if (refCountedTrapper == null) {
                LOGGER.warn("No handler of type {} found in the pipeline, this may leak ref-counted objects out of netty pipeline.",
                        RefCountedTrapper.class.getName());
            }
        }
        return context;
    }

    void setSslSession(SSLSession session) {
        sslSession = session;
    }

    @Override
    public Completable onClose() {
        return close.onClose();
    }

    @Override
    public Completable closeAsync() {
        return close.closeAsync();
    }

    @Override
    public Completable closeAsyncGracefully() {
        return close.closeAsyncGracefully();
    }
}
