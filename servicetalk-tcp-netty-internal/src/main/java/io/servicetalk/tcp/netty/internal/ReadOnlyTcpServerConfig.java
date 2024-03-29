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

import io.servicetalk.transport.api.ServiceTalkSocketOptions;
import io.servicetalk.transport.netty.internal.WireLoggingInitializer;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.util.DomainNameMapping;
import io.netty.util.DomainNameMappingBuilder;
import io.netty.util.NetUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

import static java.util.Collections.unmodifiableMap;

/**
 Read only view of {@link TcpServerConfig}.
 */
public class ReadOnlyTcpServerConfig {

    //TODO 3.x: Add back attributes
    protected final boolean autoRead;
    @SuppressWarnings("rawtypes")
    protected final Map<ChannelOption, Object> optionMap;
    protected int backlog = NetUtil.SOMAXCONN;
    @Nullable
    protected SslContext sslContext;
    protected long idleTimeoutMs;
    @Nullable
    protected DomainNameMapping<SslContext> mappings;
    @Nullable
    protected WireLoggingInitializer wireLoggingInitializer;

    /**
     * New instance.
     *
     * @param autoRead If the channels accepted by the server will have auto-read enabled.
     */
    public ReadOnlyTcpServerConfig(boolean autoRead) {
        this.autoRead = autoRead;
        optionMap = new LinkedHashMap<>();
    }

    /**
     * Copy constructor.
     *
     * @param from Source to copy from.
     */
    ReadOnlyTcpServerConfig(TcpServerConfig from) {
        autoRead = from.autoRead;
        optionMap = unmodifiableMap(new HashMap<>(from.optionMap));
        backlog = from.backlog;
        sslContext = from.sslContext;
        idleTimeoutMs = from.idleTimeoutMs;

        // Deep copy DomainNameMapping<SslContext>
        if (from.mappings != null) {
            final Map<String, SslContext> sslContextMap = from.mappings.asMap();
            final DomainNameMappingBuilder<SslContext> builder = new DomainNameMappingBuilder<>(sslContextMap.size(), from.mappings.map(null));
            sslContextMap.forEach(builder::add);
            this.mappings = builder.build();
        }

        wireLoggingInitializer = from.wireLoggingInitializer;
    }

    /**
     * Returns whether auto-read is enabled.
     *
     * @return {@code true} if auto-read enabled.
     */
    public boolean isAutoRead() {
        return autoRead;
    }

    /**
     * Returns the maximum queue length for incoming connection indications (a request to connect).
     *
     * @return Backlog.
     */
    public int getBacklog() {
        return backlog;
    }

    /**
     * Returns the {@link SslContext}.
     * @return {@link SslContext}.
     */
    @Nullable
    public SslContext getSslContext() {
        return sslContext;
    }

    /**
     * Returns the idle timeout as expressed via option {@link ServiceTalkSocketOptions#IDLE_TIMEOUT}.
     * @return idle timeout.
     */
    public long getIdleTimeoutMs() {
        return idleTimeoutMs;
    }

    /**
     * Returns the {@link ChannelOption}s for all channels accepted by the server.
     * @return Unmodifiable map of options.
     */
    public Map<ChannelOption, Object> getOptions() {
        return optionMap;
    }

    /**
     * Gets {@link DomainNameMapping}, if any.
     *
     * @return Configured mapping, {@code null} if none configured.
     */
    @Nullable
    public DomainNameMapping<SslContext> getDomainNameMapping() {
        return mappings;
    }

    /**
     * Returns the {@link WireLoggingInitializer} if any for this server.
     *
     * @return {@link WireLoggingInitializer} if any.
     */
    @Nullable
    public WireLoggingInitializer getWireLoggingInitializer() {
        return wireLoggingInitializer;
    }
}
