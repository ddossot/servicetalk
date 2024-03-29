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
package io.servicetalk.client.internal;

import io.servicetalk.concurrent.api.Publisher;

/**
 * An interface which allows controlling reserving connections which maybe used concurrently.
 */
public interface RequestConcurrencyController {
    /**
     * Attempts to reserve a connection for a single request, needs to be followed by {@link #requestFinished()}.
     * @return {@code true} if this connection is available and reserved for performing a single request.
     */
    boolean tryRequest();

    /**
     * Must be called after {@link #tryRequest()} to signify the request has completed. This method should be called
     * no more than once for each call to {@link #tryRequest()}.
     * <p>
     * Generally called from a {@link Publisher#doBeforeFinally(Runnable)} after a {@link #tryRequest()}.
     */
    void requestFinished();
}
