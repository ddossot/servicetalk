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
import io.servicetalk.concurrent.internal.SequentialCancellable;

import java.util.function.Function;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * {@link Single} as returned by {@link Single#flatMap(Function)}.
 */
final class SingleFlatMapSingle<T, R> extends AbstractAsynchronousSingleOperator<T, R> {
    private final Function<T, Single<R>> nextFactory;

    SingleFlatMapSingle(Single<T> first, Function<T, Single<R>> nextFactory, Executor executor) {
        super(first, executor);
        this.nextFactory = requireNonNull(nextFactory);
    }

    @Override
    public Subscriber<? super T> apply(final Subscriber<? super R> subscriber) {
        return new SubscriberImpl<>(subscriber, nextFactory);
    }

    private static final class SubscriberImpl<T, R> implements Subscriber<T> {
        private final Subscriber<? super R> subscriber;
        private final Function<T, Single<R>> nextFactory;
        @Nullable
        private volatile SequentialCancellable sequentialCancellable;

        SubscriberImpl(Subscriber<? super R> subscriber, Function<T, Single<R>> nextFactory) {
            this.subscriber = subscriber;
            this.nextFactory = nextFactory;
        }

        @Override
        public void onSubscribe(Cancellable cancellable) {
            SequentialCancellable sequentialCancellable = this.sequentialCancellable;
            if (sequentialCancellable != null) {
                sequentialCancellable.cancel();
                return;
            }
            this.sequentialCancellable = sequentialCancellable = new SequentialCancellable(cancellable);
            subscriber.onSubscribe(sequentialCancellable);
        }

        @Override
        public void onSuccess(@Nullable T result) {
            final SequentialCancellable sequentialCancellable = SubscriberImpl.this.sequentialCancellable;
            assert sequentialCancellable != null;

            // We can't have a class that implements Subscriber for both cases because of type erasure so just create a new object.
            final Single<R> next;
            try {
                next = requireNonNull(nextFactory.apply(result));
            } catch (Throwable cause) {
                subscriber.onError(cause);
                return;
            }

            // If this method throws we don't know if we should propagate the error to the subscriber because they
            // could have already subscribed, or subscribe async at a later time.
            next.subscribe(new Subscriber<R>() {
                @Override
                public void onSubscribe(Cancellable cancellable) {
                    sequentialCancellable.setNextCancellable(cancellable);
                }

                @Override
                public void onSuccess(@Nullable R result) {
                    subscriber.onSuccess(result);
                }

                @Override
                public void onError(Throwable t) {
                    subscriber.onError(t);
                }
            });
        }

        @Override
        public void onError(Throwable t) {
            subscriber.onError(t);
        }
    }
}
