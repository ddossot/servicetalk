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
package io.servicetalk.http.netty;

import io.servicetalk.concurrent.Cancellable;
import io.servicetalk.concurrent.api.Publisher;
import io.servicetalk.concurrent.api.Single;

import org.reactivestreams.Subscription;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static io.servicetalk.concurrent.Cancellable.IGNORE_CANCEL;
import static io.servicetalk.concurrent.internal.EmptySubscription.EMPTY_SUBSCRIPTION;
import static io.servicetalk.concurrent.internal.SubscriberUtils.checkDuplicateSubscription;
import static io.servicetalk.concurrent.internal.ThrowableUtil.unknownStackTrace;
import static java.util.Objects.requireNonNull;

/**
 * This class is responsible for splicing a {@link Publisher}&lt;{@link Object}&gt; with a common {@code Payload}
 * into a {@code Data}&lt;{@code Payload}&gt; eg. {@code StreamingHttpResponse}&lt;{@code HttpPayloadChunk}&gt;.
 *
 * @param <Data> type of container, eg. {@code StreamingHttpResponse}&lt;{@code HttpPayloadChunk}&gt;
 * @param <MetaData> type of meta-data in front of the stream of {@code Payload}, eg. {@code HttpResponseMetaData}
 * @param <Payload> type of payload inside the {@code Data}, eg. {@code HttpPayloadChunk}
 */
final class SpliceFlatStreamToMetaSingle<Data, MetaData, Payload> extends Single<Data> {

    private final BiFunction<MetaData, Publisher<Payload>, Data> packer;
    private final Publisher<?> original;

    /**
     * Operator splicing a {@link Publisher}&lt;{@link Object}&gt; with a common {@code Payload} and {@code
     * MetaData} header as first element into a {@code Data}&lt;{@code Payload}&gt; eg. {@code
     * StreamingHttpResponse}&lt;{@code HttpPayloadChunk}&gt;.
     *
     * @param original the stream of {@link Object}s to splice in a {@code Data}&lt;{@code Payload}&gt;
     * @param packer function to pack the {@link Publisher}&lt;{@code Payload}&gt; and {@code MetaData} into a
     * {@code Data}
     */
    SpliceFlatStreamToMetaSingle(Publisher<?> original, BiFunction<MetaData, Publisher<Payload>, Data> packer) {
        this.packer = requireNonNull(packer);
        this.original = requireNonNull(original);
    }

    @Override
    protected void handleSubscribe(Subscriber<? super Data> dataSubscriber) {
        original.subscribe(new SplicingSubscriber<>(this, dataSubscriber));
    }

    /**
     * Operator to flatten a {@code Data}&lt;{@code Payload}&gt; into a {@link Publisher}&lt;{@link Object}&gt;.
     *
     * @param data object containing a {@link Publisher}&lt;{@code Payload}&gt;
     * @param unpack function to unpack the {@link Publisher}&lt;{@code Payload}&gt; from the container
     * @param <Data> type of container, eg. {@code StreamingHttpResponse}&lt;{@code HttpPayloadChunk}&gt;
     * @param <Payload> type of payload inside the {@code Data}, eg. {@code HttpPayloadChunk}
     * @return a flattened {@link Publisher}&lt;{@link Object}&gt;
     */
    @SuppressWarnings("unchecked")
    static <Data, Payload> Publisher<Object> flatten(Data data, Function<Data, Publisher<Payload>> unpack) {
        return ((Publisher) Publisher.just(data)).concatWith(unpack.apply(data));
    }

    /* Visible for testing */
    static final class SplicingSubscriber<Data, MetaData, Payload> implements org.reactivestreams.Subscriber<Object> {

        private static final AtomicReferenceFieldUpdater<SplicingSubscriber, Object>
                maybePayloadSubUpdater = AtomicReferenceFieldUpdater.newUpdater(SplicingSubscriber.class,
                Object.class, "maybePayloadSub");

        private static final Throwable CANCELED =
                unknownStackTrace(new CancellationException("Canceled prematurely from Data"),
                        SplicingSubscriber.class, "cancelData(..)");
        private static final String PENDING = "PENDING";
        private static final String EMPTY_COMPLETED = "EMPTY_COMPLETED";
        private static final String EMPTY_COMPLETED_DELIVERED = "EMPTY_COMPLETED_DELIVERED";

        /**
         * A field that assumes various types and states depending on the state of the operator.
         * <p>
         * One of <ul>
         *     <li>{@code null} – initial pending state before the {@link Single} is completed</li>
         *     <li>{@link org.reactivestreams.Subscriber}&lt;{@code Payload}&gt; - when subscribed to the payload</li>
         *     <li>{@link #CANCELED} - when the {@link Single} is canceled prematurely</li>
         *     <li>{@link #PENDING} - when the {@link Single} will complete and {@code Payload} pending subscribe</li>
         *     <li>{@link #EMPTY_COMPLETED} - when the stream completed prematurely (empty) payload</li>
         *     <li>{@link #EMPTY_COMPLETED_DELIVERED} - when the premature (empty) completion event was delivered to a
         *     subscriber</li>
         *     <li>{@link Throwable} - the error that occurred in the stream</li>
         * </ul>
         */
        @Nullable
        @SuppressWarnings({"unused", "unchecked"})
        private volatile Object maybePayloadSub;

        /**
         * Once a {@link #maybePayloadSub} is set to a {@link org.reactivestreams.Subscriber} we cache a copy in a
         * non-volatile field to allow caching in register and avoid instanceof and casting on the hot path.
         */
        @Nullable
        private org.reactivestreams.Subscriber<Payload> payloadSubscriber;

        /**
         * Indicates whether the meta-data has been observed.
         */
        private boolean metaSeenInOnNext;

        /**
         * The {@link Subscription} before wrapping to pass it to the downstream {@link org.reactivestreams.Subscriber}.
         * Doesn't need to be {@code volatile}, as it should be visible wrt JMM according to
         * <a href="https://github.com/reactive-streams/reactive-streams-jvm/blob/v1.0.2/README.md#2.11>RS spec 2.11</a>
         */
        @Nullable
        private Subscription rawSubscription;

        /**
         * We request-1 first and then send {@link Subscriber#onSubscribe(Cancellable)} to {@code dataSubscriber}.
         * If request-1 synchronously delivers {@link #onNext(Object)}, then we may send
         * {@link Subscriber#onSuccess(Object)} before onSubscribe.
         * This state makes sure we always send onSubscribe first and only once.
         */
        private boolean onSubscribeSent;

        private final SpliceFlatStreamToMetaSingle<Data, MetaData, Payload> parent;
        private final Subscriber<? super Data> dataSubscriber;

        /**
         * This operator subscribes to one stream and forks into a {@link Single} and {@link Publisher}, so
         * the only risk for concurrently accessing the upstream {@link Subscription} is when the {@link Single}
         * gets canceled after the contained {@link Publisher} is subscribed. To avoid this problem we guard it with
         * a CAS on {@link Single} termination.
         *
         * @param parent reference to the parent class holding immutable state
         * @param dataSubscriber {@link Subscriber} to the {@code Data}
         */
        private SplicingSubscriber(SpliceFlatStreamToMetaSingle<Data, MetaData, Payload> parent,
                                   Subscriber<? super Data> dataSubscriber) {
            this.parent = parent;
            this.dataSubscriber = dataSubscriber;
        }

        /**
         * This cancels the {@link Subscription} of the upstream {@link Publisher}&lt;{@link Object}&gt; unless this
         * {@link Single} has already terminated.
         * <p>
         * Guarded by the CAS to avoid concurrency with the {@link Subscription} on the contained {@link
         * Publisher}&lt;{@code Payload}&gt;
         */
        private void cancelData(Subscription subscription) {
            if (maybePayloadSubUpdater.compareAndSet(this, null, CANCELED)) {
                subscription.cancel();
            }
        }

        @Override
        public void onSubscribe(final Subscription inStreamSubscription) {
            if (!checkDuplicateSubscription(rawSubscription, inStreamSubscription)) {
                return;
            }
            rawSubscription = inStreamSubscription;
            // get the first element a MetaData that we consume to complete the Single<Data>
            rawSubscription.request(1);
            if (!onSubscribeSent) {
                onSubscribeSent = true;
                dataSubscriber.onSubscribe(() -> cancelData(inStreamSubscription));
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onNext(Object obj) {
            if (metaSeenInOnNext) {
                Payload payload = (Payload) obj;
                if (payloadSubscriber != null) {
                    payloadSubscriber.onNext(payload);
                } else {
                    final Object subscriber = maybePayloadSub;
                    if (subscriber instanceof org.reactivestreams.Subscriber) {
                        payloadSubscriber = ((org.reactivestreams.Subscriber<Payload>) subscriber);
                        payloadSubscriber.onNext(payload);
                    }
                }
            } else {
                if (!onSubscribeSent) {
                    onSubscribeSent = true;
                    // Since we are going to deliver data after this, there is no need for this to be cancellable.
                    dataSubscriber.onSubscribe(IGNORE_CANCEL);
                }
                MetaData meta = (MetaData) obj;
                // When the upstream Publisher is canceled we don't give it to any Payload Subscribers
                metaSeenInOnNext = true;
                final Data data;
                try {
                    data = parent.packer.apply(meta,
                            maybePayloadSubUpdater.compareAndSet(this, null, PENDING) ?
                                    newPayloadPublisher() : Publisher.error(CANCELED));
                } catch (Throwable t) {
                    assert rawSubscription != null;
                    // We know that there is nothing else that can happen on this stream as we are not sending the
                    // data to the dataSubscriber.
                    rawSubscription.cancel();
                    // Since we update our internal state before calling parent.packer, if parent.packer throws,
                    // it will cause the assumptions to break in onError(). So, we catch and handle the error ourselves
                    // as opposed to let the source call onError.
                    dataSubscriber.onError(t);
                    return;
                }
                dataSubscriber.onSuccess(data);
            }
        }

        @Nonnull
        private Publisher<Payload> newPayloadPublisher() {
            return new Publisher<Payload>() {
                @Override
                protected void handleSubscribe(org.reactivestreams.Subscriber<? super Payload> newSubscriber) {
                    if (maybePayloadSubUpdater.compareAndSet(SplicingSubscriber.this, PENDING, newSubscriber)) {
                        // TODO risk of a race here with terminal events, will be addressed in follow-up PR
                        newSubscriber.onSubscribe(rawSubscription);
                    } else {
                        // Entering this branch means either a duplicate subscriber or a stream that completed or failed
                        // without a subscriber present. The consequence is that unless we've seen payload data we may
                        // not send onComplete() or onError() to the original subscriber, but that is OK as long as one
                        // subscriber of them gets the correct signal and all others get a duplicate subscriber error.
                        final Object maybeSubscriber = SplicingSubscriber.this.maybePayloadSub;
                        newSubscriber.onSubscribe(EMPTY_SUBSCRIPTION);
                        if (maybeSubscriber == EMPTY_COMPLETED && maybePayloadSubUpdater
                                .compareAndSet(SplicingSubscriber.this, EMPTY_COMPLETED, EMPTY_COMPLETED_DELIVERED)) {
                            // Prematurely completed (header + empty payload)
                            newSubscriber.onComplete();
                        } else if (maybeSubscriber instanceof Throwable && maybePayloadSubUpdater
                                .compareAndSet(SplicingSubscriber.this, maybeSubscriber, EMPTY_COMPLETED_DELIVERED)) {
                            // Premature error or cancel
                            newSubscriber.onError((Throwable) maybeSubscriber);
                        } else {
                            // Existing subscriber or terminal event consumed by other subscriber (COMPLETED_DELIVERED)
                            newSubscriber.onError(new IllegalStateException("Duplicate Subscribers are not allowed. " +
                                    "Existing: " + maybeSubscriber + ", new: " + newSubscriber));
                        }
                    }
                }
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onError(Throwable t) {
            if (payloadSubscriber != null) { // We have a subscriber that has seen onNext()
                payloadSubscriber.onError(t);
            } else {
                final Object maybeSubscriber = maybePayloadSubUpdater.getAndSet(this, t);
                if (maybeSubscriber == CANCELED || !metaSeenInOnNext) {
                    dataSubscriber.onError(t);
                } else if (maybeSubscriber instanceof org.reactivestreams.Subscriber) {
                    if (maybePayloadSubUpdater.compareAndSet(SplicingSubscriber.this, t, EMPTY_COMPLETED_DELIVERED)) {
                        ((org.reactivestreams.Subscriber<Payload>) maybeSubscriber).onError(t);
                    } else {
                        ((org.reactivestreams.Subscriber<Payload>) maybeSubscriber).onError(new IllegalStateException(
                                "Duplicate Subscribers are not allowed. Existing: " + maybeSubscriber +
                                        ", failed the race with a duplicate, but neither has seen onNext()"));
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onComplete() {
            if (payloadSubscriber != null) { // We have a subscriber that has seen onNext()
                payloadSubscriber.onComplete();
            } else {
                final Object maybeSubscriber = maybePayloadSubUpdater.getAndSet(this, EMPTY_COMPLETED);
                if (maybeSubscriber instanceof org.reactivestreams.Subscriber) {
                    if (maybePayloadSubUpdater.compareAndSet(SplicingSubscriber.this, EMPTY_COMPLETED,
                            EMPTY_COMPLETED_DELIVERED)) {
                        ((org.reactivestreams.Subscriber<Payload>) maybeSubscriber).onComplete();
                    } else {
                        ((org.reactivestreams.Subscriber<Payload>) maybeSubscriber).onError(new IllegalStateException(
                                "Duplicate Subscribers are not allowed. Existing: " + maybeSubscriber +
                                        ", failed the race with a duplicate, but neither has seen onNext()"));
                    }
                } else if (!metaSeenInOnNext) {
                    dataSubscriber.onError(new IllegalStateException("Empty stream"));
                }
            }
        }
    }
}
