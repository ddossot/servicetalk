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

import io.servicetalk.client.api.LoadBalancer;
import io.servicetalk.client.api.ServiceDiscoverer;
import io.servicetalk.http.api.HttpHeaderNames;
import io.servicetalk.http.api.StreamingHttpClient;
import io.servicetalk.http.api.StreamingHttpRequest;
import io.servicetalk.transport.api.HostAndPort;

import java.net.InetSocketAddress;

import static io.servicetalk.http.netty.DefaultSingleAddressHttpClientBuilder.forUnknownHostAndPort;

/**
 * Factory methods for building {@link StreamingHttpClient} instances.
 */
public final class HttpClients {

    private HttpClients() {
        // No instances
    }

    /**
     * Creates a {@link MultiAddressHttpClientBuilder} for clients capable of parsing an <a
     * href="https://tools.ietf.org/html/rfc7230#section-5.3.2">absolute-form URL</a>, connecting to multiple addresses
     * with default {@link LoadBalancer} and DNS {@link ServiceDiscoverer}.
     * <p>
     * When a <a href="https://tools.ietf.org/html/rfc3986#section-4.2">relative URL</a> is passed in the {@link
     * StreamingHttpRequest#setRequestTarget(String)} this client requires a {@link HttpHeaderNames#HOST} present in order to
     * infer the remote address.
     * @return new builder with default configuration
     */
    public static MultiAddressHttpClientBuilder<HostAndPort, InetSocketAddress> forMultiAddressUrl() {
        return new DefaultMultiAddressUrlHttpClientBuilder(forUnknownHostAndPort());
    }

    /**
     * Creates a {@link SingleAddressHttpClientBuilder} for an address with default {@link LoadBalancer} and DNS {@link
     * ServiceDiscoverer}.
     *
     * @param host host to connect to, resolved by default using a DNS {@link ServiceDiscoverer}. This will also be
     * used for the {@link HttpHeaderNames#HOST} together with the {@code port}. Use this method {@link
     * SingleAddressHttpClientBuilder#enableHostHeaderFallback(CharSequence)} if you want to override that value or
     * {@link BaseHttpClientBuilder#disableHostHeaderFallback()} if you want to disable this behavior.
     * @param port port to connect to
     * @return new builder for the address
     */
    public static SingleAddressHttpClientBuilder<HostAndPort, InetSocketAddress> forSingleAddress(
            final String host, final int port) {
        return forSingleAddress(HostAndPort.of(host, port));
    }

    /**
     * Creates a {@link SingleAddressHttpClientBuilder} for an address with default {@link LoadBalancer} and DNS {@link
     * ServiceDiscoverer}.
     *
     * @param address the {@code UnresolvedAddress} to connect to, resolved by default using a DNS {@link
     * ServiceDiscoverer}. This address will also be used for the {@link HttpHeaderNames#HOST}. Use this method {@link
     * SingleAddressHttpClientBuilder#enableHostHeaderFallback(CharSequence)} if you want to override that value or
     * {@link BaseHttpClientBuilder#disableHostHeaderFallback()} if you want to disable this behavior.
     * @return new builder for the address
     */
    public static SingleAddressHttpClientBuilder<HostAndPort, InetSocketAddress> forSingleAddress(
            final HostAndPort address) {
        return DefaultSingleAddressHttpClientBuilder.forHostAndPort(address);
    }

    /**
     * Creates a {@link SingleAddressHttpClientBuilder} for a custom address type with default {@link LoadBalancer} and
     * user provided {@link ServiceDiscoverer}.
     *
     * @param serviceDiscoverer The {@link ServiceDiscoverer} to resolve addresses of remote servers to connect to.
     * The lifecycle of the provided {@link ServiceDiscoverer} should be managed by the caller.
     * @param address the {@code UnresolvedAddress} to connect to resolved using the provided {@code serviceDiscoverer}.
     * This address will also be used for the {@link HttpHeaderNames#HOST} using a best effort conversion. Use {@link
     * SingleAddressHttpClientBuilder#enableHostHeaderFallback(CharSequence)} if you want to override that value or
     * {@link BaseHttpClientBuilder#disableHostHeaderFallback()} if you want to disable this behavior.
     * @param <U> the type of address before resolution (unresolved address)
     * @param <R> the type of address after resolution (resolved address)
     * @return new builder with provided configuration
     */
    public static <U, R> SingleAddressHttpClientBuilder<U, R> forSingleAddress(
            final ServiceDiscoverer<U, R> serviceDiscoverer,
            final U address) {
        return new DefaultSingleAddressHttpClientBuilder<>(serviceDiscoverer, address);
    }
}
