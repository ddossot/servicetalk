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

import io.servicetalk.concurrent.Cancellable;
import io.servicetalk.concurrent.internal.SignalOffloader;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

final class SingleToCompletable<T> extends AbstractNoHandleSubscribeCompletable {
    private final Single<T> original;

    SingleToCompletable(Single<T> original, Executor executor) {
        super(executor);
        this.original = requireNonNull(original);
    }

    @Override
    void handleSubscribe(final Subscriber subscriber, final SignalOffloader signalOffloader) {
        original.subscribe(new Single.Subscriber<T>() {
            @Override
            public void onSubscribe(Cancellable cancellable) {
                subscriber.onSubscribe(cancellable);
            }

            @Override
            public void onSuccess(@Nullable T result) {
                subscriber.onComplete();
            }

            @Override
            public void onError(Throwable t) {
                subscriber.onError(t);
            }
        },
                // Since this is converting a Single to a Completable, we should try to use the same SignalOffloader for
                // subscribing to the original Single to avoid thread hop. Since, it is the same source, just viewed as
                // a Completable, there is no additional risk of deadlock.
                signalOffloader);
    }
}
