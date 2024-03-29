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

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * {@link Single} as returned by {@link Single#flatMapPublisher(Function)}.
 */
final class SingleFlatMapPublisher<T, R> extends AbstractNoHandleSubscribePublisher<R> {
    private final Single<T> original;
    private final Function<T, Publisher<R>> nextFactory;

    SingleFlatMapPublisher(Single<T> original, Function<T, Publisher<R>> nextFactory, Executor executor) {
        super(executor);
        this.original = requireNonNull(original);
        this.nextFactory = requireNonNull(nextFactory);
    }

    @Override
    void handleSubscribe(final Subscriber<? super R> subscriber, final SignalOffloader signalOffloader) {
        original.subscribe(new SubscriberImpl<>(subscriber, nextFactory, signalOffloader), signalOffloader);
    }

    private static final class SubscriberImpl<T, R> implements Single.Subscriber<T>, org.reactivestreams.Subscriber<R> {
        private final Subscriber<? super R> subscriber;
        private final Function<T, Publisher<R>> nextFactory;
        private final SignalOffloader signalOffloader;
        @Nullable
        private volatile SequentialSubscription sequentialSubscription;

        SubscriberImpl(Subscriber<? super R> subscriber, Function<T, Publisher<R>> nextFactory,
                       final SignalOffloader signalOffloader) {
            this.subscriber = subscriber;
            this.nextFactory = requireNonNull(nextFactory);
            this.signalOffloader = signalOffloader;
        }

        @Override
        public void onSubscribe(Cancellable cancellable) {
            SequentialSubscription sequentialSubscription = this.sequentialSubscription;
            if (sequentialSubscription != null) {
                sequentialSubscription.cancel();
                return;
            }
            this.sequentialSubscription = sequentialSubscription = new SequentialSubscription(new Subscription() {
                @Override
                public void request(long n) {
                    // This is a NOOP because the work for the original Single is already in progress when onSubscribe
                    // is called. Demand for Single is implicit so there is nothing to request.
                }

                @Override
                public void cancel() {
                    cancellable.cancel();
                }
            });
            subscriber.onSubscribe(sequentialSubscription);
        }

        @Override
        public void onSubscribe(Subscription s) {
            SequentialSubscription sequentialSubscription = this.sequentialSubscription;
            assert sequentialSubscription != null;
            sequentialSubscription.switchTo(s);
        }

        @Override
        public void onSuccess(@Nullable T result) {
            final Publisher<R> next;
            try {
                next = requireNonNull(nextFactory.apply(result));
            } catch (Throwable cause) {
                subscriber.onError(cause);
                return;
            }
            // We need to preserve the threading semantics for the original subscriber. Since here we are subscribing to
            // a new source which may have different threading semantics, we explicitly offload signals going down to
            // the original subscriber. If we do not do this and next source does not support blocking operations,
            // whereas original subscriber does, we will violate threading assumptions.
            next.subscribe(signalOffloader.offloadSubscriber((Subscriber<R>) this));
        }

        @Override
        public void onNext(R r) {
            SequentialSubscription sequentialSubscription = this.sequentialSubscription;
            assert sequentialSubscription != null;
            sequentialSubscription.itemReceived();
            subscriber.onNext(r);
        }

        @Override
        public void onError(Throwable t) {
            subscriber.onError(t);
        }

        @Override
        public void onComplete() {
            subscriber.onComplete();
        }
    }
}
