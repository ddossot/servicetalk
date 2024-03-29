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

import io.servicetalk.transport.api.ConnectionContext;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.annotation.Nullable;

import static io.servicetalk.transport.netty.internal.SslUtils.newHandler;
import static java.util.Objects.requireNonNull;

/**
 * SSL {@link ChannelInitializer} for clients.
 */
public class SslClientChannelInitializer extends AbstractSslChannelInitializer {

    @Nullable
    private final String hostnameVerificationAlgorithm;
    @Nullable
    private final String hostnameVerificationHost;
    private final int hostnameVerificationPort;
    private final SslContext sslContext;

    /**
     * New instance.
     * @param sslContext to use for configuring SSL.
     * @param hostnameVerificationAlgorithm hostname verification algorithm.
     * @param hostnameVerificationHost the non-authoritative name of the host.
     * @param hostnameVerificationPort the non-authoritative port.
     */
    public SslClientChannelInitializer(SslContext sslContext, @Nullable String hostnameVerificationAlgorithm,
                                       @Nullable String hostnameVerificationHost, int hostnameVerificationPort) {
        this.sslContext = requireNonNull(sslContext);
        this.hostnameVerificationAlgorithm = hostnameVerificationAlgorithm;
        this.hostnameVerificationHost = hostnameVerificationHost;
        this.hostnameVerificationPort = hostnameVerificationPort;
    }

    @Nullable
    @Override
    protected SslHandler addNettySslHandler(Channel channel, ConnectionContext context) {
        SslHandler sslHandler = newHandler(sslContext, channel.alloc(), hostnameVerificationAlgorithm,
                                           hostnameVerificationHost, hostnameVerificationPort);
        channel.pipeline().addFirst(sslHandler);
        return sslHandler;
    }
}
