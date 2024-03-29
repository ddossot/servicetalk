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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.atomic.AtomicIntegerFieldUpdater.newUpdater;
import static java.util.function.Function.identity;

/**
 * A utility class to create {@link AsyncCloseable}s.
 */
public final class AsyncCloseables {

    private AsyncCloseables() {
        // No instances.
    }

    /**
     * Invokes {@link AsyncCloseable#closeAsyncGracefully()} on the {@code closable}, applies a timeout, and if the
     * timeout fires forces a call to {@link AsyncCloseable#closeAsync()}.
     * @param closable The {@link AsyncCloseable} to initiate {@link AsyncCloseable#closeAsyncGracefully()} on.
     * @param timeout The timeout duration to wait for {@link AsyncCloseable#closeAsyncGracefully()} to complete.
     * @param timeoutUnit The time unit applied to {@code timeout}.
     * @return A {@link Completable} that is notified once the close is complete.
     */
    public static Completable closeAsyncGracefully(AsyncCloseable closable, long timeout, TimeUnit timeoutUnit) {
        return closable.closeAsyncGracefully().timeout(timeout, timeoutUnit).onErrorResume(
                t -> t instanceof TimeoutException ? closable.closeAsync() : Completable.error(t)
        );
    }

    /**
     * Creates an empty {@link ListenableAsyncCloseable} that does nothing when
     * {@link ListenableAsyncCloseable#closeAsync()} apart from completing the {@link ListenableAsyncCloseable#onClose()}.
     *
     * @return A new {@link ListenableAsyncCloseable}.
     */
    public static ListenableAsyncCloseable emptyAsyncCloseable() {
        return new ListenableAsyncCloseable() {

            private final CompletableProcessor onClose = new CompletableProcessor();
            private final Completable closeAsync = new Completable() {
                @Override
                protected void handleSubscribe(final Subscriber subscriber) {
                    onClose.onComplete();
                    onClose.subscribe(subscriber);
                }
            };

            @Override
            public Completable closeAsync() {
                return closeAsync;
            }

            @Override
            public Completable onClose() {
                return onClose;
            }
        };
    }

    /**
     * Wraps the passed {@link AsyncCloseable} and creates a new {@link ListenableAsyncCloseable}.
     * This method owns the passed {@link AsyncCloseable} after this method returns and hence it should not be used by
     * the caller after this method returns.
     *
     * @param asyncCloseable {@link AsyncCloseable} to convert to {@link ListenableAsyncCloseable}.
     * @return A new {@link ListenableAsyncCloseable}.
     */
    public static ListenableAsyncCloseable toListenableAsyncCloseable(AsyncCloseable asyncCloseable) {
        return toListenableAsyncCloseable(asyncCloseable, identity());
    }

    /**
     * Wraps the passed {@link AsyncCloseable} and creates a new {@link ListenableAsyncCloseable}.
     * This method owns the passed {@link AsyncCloseable} after this method returns and hence it should not be used by
     * the caller after this method returns.
     *
     * @param asyncCloseable {@link AsyncCloseable} to convert to {@link ListenableAsyncCloseable}.
     * @param onCloseDecorator {@link Function} that can decorate the {@link Completable} returned from the returned
     * {@link ListenableAsyncCloseable#onClose()}.
     * @return A new {@link ListenableAsyncCloseable}.
     */
    public static ListenableAsyncCloseable toListenableAsyncCloseable(
            AsyncCloseable asyncCloseable, Function<Completable, Completable> onCloseDecorator) {
        return new ListenableAsyncCloseable() {

            private final CompletableProcessor onCloseProcessor = new CompletableProcessor();
            private final Completable onClose = onCloseDecorator.apply(onCloseProcessor);

            @Override
            public Completable closeAsyncGracefully() {
                return new Completable() {
                    @Override
                    protected void handleSubscribe(final Subscriber subscriber) {
                        asyncCloseable.closeAsyncGracefully().subscribe(onCloseProcessor);
                        onClose.subscribe(subscriber);
                    }
                };
            }

            @Override
            public Completable onClose() {
                return onClose;
            }

            @Override
            public Completable closeAsync() {
                return new Completable() {
                    @Override
                    protected void handleSubscribe(final Subscriber subscriber) {
                        asyncCloseable.closeAsync().subscribe(onCloseProcessor);
                        onClose.subscribe(subscriber);
                    }
                };
            }
        };
    }

    /**
     * Creates a new {@link ListenableAsyncCloseable} which uses the passed {@link Supplier} to get the implementation
     * of close.
     *
     * @param close {@link Supplier} of {@link Completable} that represents close operation. This will be called when
     * the returned {@link ListenableAsyncCloseable} is subscribed for the first time.
     * @return A new {@link ListenableAsyncCloseable}.
     */
    public static ListenableAsyncCloseable toAsyncCloseable(Supplier<Completable> close) {
        return new DefaultAsyncCloseable(close);
    }

    /**
     * Creates a new {@link CompositeCloseable}.
     *
     * @return A new {@link CompositeCloseable}.
     */
    public static CompositeCloseable newCompositeCloseable() {
        return new DefaultCompositeCloseable();
    }

    private static final class DefaultAsyncCloseable implements ListenableAsyncCloseable {

        private static final AtomicIntegerFieldUpdater<DefaultAsyncCloseable> closedUpdater =
                newUpdater(DefaultAsyncCloseable.class, "closed");
        private final Supplier<Completable> closeImpl;
        private final CompletableProcessor onClose = new CompletableProcessor();

        @SuppressWarnings("unused")
        private volatile int closed;

        DefaultAsyncCloseable(final Supplier<Completable> closeImpl) {
            this.closeImpl = closeImpl;
        }

        @Override
        public Completable closeAsync() {
            return new Completable() {
                @Override
                protected void handleSubscribe(final Subscriber subscriber) {
                    onClose.subscribe(subscriber);
                    if (closedUpdater.compareAndSet(DefaultAsyncCloseable.this, 0, 1)) {
                        closeImpl.get().subscribe(onClose);
                    }
                }
            };
        }

        @Override
        public Completable onClose() {
            return onClose;
        }
    }
}
