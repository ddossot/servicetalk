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

import io.servicetalk.concurrent.Cancellable;
import io.servicetalk.concurrent.api.Completable;
import io.servicetalk.concurrent.api.Publisher;
import io.servicetalk.concurrent.internal.LatestValueSubscriber;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static io.servicetalk.concurrent.Cancellable.IGNORE_CANCEL;
import static java.util.concurrent.atomic.AtomicIntegerFieldUpdater.newUpdater;

abstract class AbstractReservableRequestConcurrencyController implements ReservableRequestConcurrencyController {
    private static final AtomicIntegerFieldUpdater<AbstractReservableRequestConcurrencyController>
            pendingRequestsUpdater = newUpdater(AbstractReservableRequestConcurrencyController.class,
            "pendingRequests");

    private static final int STATE_QUIT = -2;
    private static final int STATE_RESERVED = -1;
    private static final int STATE_IDLE = 0;

    /*
     * Following semantics:
     * STATE_RESERVED if this is reserved.
     * STATE_QUIT if quit command issued.
     * STATE_IDLE if connection is not used.
     * pending request count if none of the above states.
     */
    @SuppressWarnings("unused")
    private volatile int pendingRequests;
    private final LatestValueSubscriber<Integer> maxConcurrencyHolder;

    AbstractReservableRequestConcurrencyController(final Publisher<Integer> maxConcurrencySettingStream,
                                                   final Completable onClose) {
        maxConcurrencyHolder = new LatestValueSubscriber<>();
        maxConcurrencySettingStream.subscribe(maxConcurrencyHolder);
        onClose.subscribe(new Completable.Subscriber() {
            @Override
            public void onSubscribe(Cancellable cancellable) {
                // No op
            }

            @Override
            public void onComplete() {
                pendingRequests = STATE_QUIT;
            }

            @Override
            public void onError(Throwable ignored) {
                pendingRequests = STATE_QUIT;
            }
        });
    }

    @Override
    public final void requestFinished() {
        pendingRequestsUpdater.decrementAndGet(this);
    }

    @Override
    public boolean tryReserve() {
        return pendingRequestsUpdater.compareAndSet(this, STATE_IDLE, STATE_RESERVED);
    }

    @Override
    public Completable releaseAsync() {
        return new Completable() {
            @Override
            protected void handleSubscribe(Subscriber subscriber) {
                subscriber.onSubscribe(IGNORE_CANCEL);
                // Ownership is maintained by the caller.
                if (pendingRequestsUpdater.compareAndSet(AbstractReservableRequestConcurrencyController.this,
                        STATE_RESERVED, STATE_IDLE)) {
                    subscriber.onComplete();
                } else {
                    subscriber.onError(new IllegalStateException("Resource " + this +
                            (pendingRequests == STATE_QUIT ? " is closed." : " was not reserved.")));
                }
            }
        };
    }

    final int getLastSeenMaxValue(int defaultValue) {
        return maxConcurrencyHolder.getLastSeenValue(defaultValue);
    }

    final int getPendingRequests() {
        return pendingRequests;
    }

    final boolean casPendingRequests(int oldValue, int newValue) {
        return pendingRequestsUpdater.compareAndSet(this, oldValue, newValue);
    }
}
