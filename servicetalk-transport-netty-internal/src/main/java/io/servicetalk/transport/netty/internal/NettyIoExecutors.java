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

import io.servicetalk.transport.api.IoExecutor;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.ThreadFactory;

import static java.util.Objects.requireNonNull;

/**
 * A static factory to create or convert to {@link NettyIoExecutor}.
 */
public final class NettyIoExecutors {

    private NettyIoExecutors() {
        // No instances.
    }

    /**
     * Create a new {@link NettyIoExecutor}.
     *
     * @param ioThreads number of threads
     * @param threadFactory the {@link ThreadFactory} to use.
     * @return The created {@link IoExecutor}
     */
    public static NettyIoExecutor createIoExecutor(int ioThreads, ThreadFactory threadFactory) {
        return new EventLoopGroupIoExecutor(Epoll.isAvailable() ? new EpollEventLoopGroup(ioThreads, threadFactory) :
                KQueue.isAvailable() ? new KQueueEventLoopGroup(ioThreads, threadFactory) :
                        new NioEventLoopGroup(ioThreads, threadFactory), true);
    }

    /**
     * Attempts to convert the passed {@link IoExecutor} to a {@link NettyIoExecutor}.
     *
     * @param ioExecutor {@link IoExecutor} to convert.
     * @return {@link NettyIoExecutor} corresponding to the passed {@link IoExecutor}.
     * @throws IllegalArgumentException If {@link IoExecutor} is not of type {@link NettyIoExecutor}.
     */
    public static NettyIoExecutor toNettyIoExecutor(IoExecutor ioExecutor) {
        requireNonNull(ioExecutor);
        if (ioExecutor instanceof NettyIoExecutor) {
            return (NettyIoExecutor) ioExecutor;
        }
        throw new IllegalArgumentException("Incompatible IoExecutor: " + ioExecutor +
                ". Not a netty based IoExecutor.");
    }

    /**
     * Creates a new instance of {@link NettyIoExecutor} using the passed {@link EventLoop}.
     *
     * @param eventLoop {@link EventLoop} to use to create a new {@link NettyIoExecutor}.
     * @return New {@link NettyIoExecutor} using the passed {@link EventLoop}.
     */
    public static NettyIoExecutor fromNettyEventLoop(EventLoop eventLoop) {
        return new EventLoopIoExecutor(eventLoop, true);
    }

    /**
     * Creates a new instance of {@link NettyIoExecutor} using the passed {@link EventLoopGroup}.
     *
     * @param eventLoopGroup {@link EventLoopGroup} to use to create a new {@link NettyIoExecutor}.
     * @return New {@link NettyIoExecutor} using the passed {@link EventLoopGroup}.
     */
    public static NettyIoExecutor fromNettyEventLoopGroup(EventLoopGroup eventLoopGroup) {
        return new EventLoopGroupIoExecutor(eventLoopGroup, true);
    }
}
