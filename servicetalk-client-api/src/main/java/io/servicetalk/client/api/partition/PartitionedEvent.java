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
package io.servicetalk.client.api.partition;

import static io.servicetalk.client.api.ServiceDiscoverer.Event;

/**
 * An {@link Event} which is associated with a partition.
 * @param <ResolvedAddress> The type of resolved address.
 */
public interface PartitionedEvent<ResolvedAddress> extends Event<ResolvedAddress> {
    /**
     * Get the fully specified {@link PartitionAttributes} that uniquely identifies {@link #getAddress()}.
     * @return the fully specified {@link PartitionAttributes} that uniquely identifies {@link #getAddress()}.
     */
    PartitionAttributes getPartitionAddress();
}
