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
package io.servicetalk.data.jackson.jersey.internal;

import io.servicetalk.data.jackson.jersey.ServiceTalkJacksonSerializerFeature;

import org.glassfish.jersey.internal.spi.AutoDiscoverable;

import javax.annotation.Priority;
import javax.ws.rs.core.FeatureContext;

import static org.glassfish.jersey.internal.spi.AutoDiscoverable.DEFAULT_PRIORITY;

/**
 * {@link AutoDiscoverable} registering {@link ServiceTalkJacksonSerializerFeature} if the feature is not already
 * registered.
 */
@Priority(DEFAULT_PRIORITY)
public final class ServiceTalkJacksonSerializerAutoDiscoverable implements AutoDiscoverable {
    @Override
    public void configure(final FeatureContext context) {
        if (!context.getConfiguration().isRegistered(ServiceTalkJacksonSerializerFeature.class)) {
            context.register(ServiceTalkJacksonSerializerFeature.class);
        }
    }
}
