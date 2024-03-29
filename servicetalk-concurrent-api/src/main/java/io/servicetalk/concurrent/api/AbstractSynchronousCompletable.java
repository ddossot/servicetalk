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
package io.servicetalk.concurrent.api;

import io.servicetalk.concurrent.internal.SignalOffloader;

/**
 * Base class for all {@link Completable}s that are created with already realized result and does not generate result
 * asynchronously.
 */
abstract class AbstractSynchronousCompletable extends AbstractNoHandleSubscribeCompletable {

    @Override
    final void handleSubscribe(Subscriber subscriber, SignalOffloader signalOffloader) {
        // Wrap the passed Subscriber with the SignalOffloader to make sure they are not invoked in the thread that
        // asynchronously processes signals and hence may not be safe to execute user code.
        // This saves the offload of doSubscribe since we know it is synchronous.
        doSubscribe(signalOffloader.offloadSubscriber(subscriber));
    }

    /**
     * Handles the subscribe call.
     *
     *  @param subscriber {@link Subscriber} to this {@link Completable}.
     */
    abstract void doSubscribe(Subscriber subscriber);
}
