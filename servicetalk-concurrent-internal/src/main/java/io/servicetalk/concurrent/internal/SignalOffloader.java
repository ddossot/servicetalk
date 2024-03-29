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
package io.servicetalk.concurrent.internal;

import io.servicetalk.concurrent.Cancellable;
import io.servicetalk.concurrent.Completable;
import io.servicetalk.concurrent.Single;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Consumer;

/**
 * A contract to offload <a
 * href="https://github.com/reactive-streams/reactive-streams-jvm/blob/v1.0.2/README.md#glossary">signals</a> to and
 * from any asynchronous source.
 *
 * <h2>Caution</h2>
 * A {@link SignalOffloader} instance <strong>MUST</strong> only be used for a single asynchronous execution chain at
 * any given time. Reusing it across different execution chains concurrently may result in deadlock.
 * Concurrent invocation of any {@link SignalOffloader} methods may result in deadlock.
 */
public interface SignalOffloader {

    /**
     * Decorates the passed {@link Subscriber} such that all method calls to it will be offloaded.
     *
     * <h2>Caution</h2>
     * This method MUST not be called concurrently with itself or other offload methods here on the same
     * {@link SignalOffloader} instance.
     *
     * @param subscriber {@link Subscriber} for which the signals are to be offloaded.
     * @param <T> Type of items received by the passed and returned {@link Subscriber}.
     * @return New {@link Subscriber} that will offload signals to the passed {@link Subscriber}.
     */
    <T> Subscriber<? super T> offloadSubscriber(Subscriber<? super T> subscriber);

    /**
     * Decorates the passed {@link Single.Subscriber} such that all method calls to it will be offloaded.
     * <h2>Caution</h2>
     * This method MUST not be called concurrently with itself or other offload methods here on the same
     * {@link SignalOffloader} instance.
     *
     * @param subscriber {@link Single.Subscriber} for which the signals are to be offloaded.
     * @param <T> Type of items received by the passed and returned {@link Single.Subscriber}.
     * @return New {@link Single.Subscriber} that will offload signals to the passed {@link Single.Subscriber}.
     */
    <T> Single.Subscriber<? super T> offloadSubscriber(Single.Subscriber<? super T> subscriber);

    /**
     * Decorates the passed {@link Completable.Subscriber} such that all method calls to it will be offloaded.
     * <h2>Caution</h2>
     * This method MUST not be called concurrently with itself or other offload methods here on the same
     * {@link SignalOffloader} instance.
     *
     * @param subscriber {@link Completable.Subscriber} for which the signals are to be offloaded.
     * @return New {@link Completable.Subscriber} that will offload signals to the passed {@link Completable.Subscriber}.
     */
    Completable.Subscriber offloadSubscriber(Completable.Subscriber subscriber);

    /**
     * Decorates the passed {@link Subscriber} such that all method calls to its {@link Subscription} will be offloaded.
     * <em>None of the {@link Subscriber} methods will be offloaded.</em>
     * <h2>Caution</h2>
     * This method MUST not be called concurrently with itself or other offload methods here on the same
     * {@link SignalOffloader} instance.
     *
     * @param subscriber {@link Subscriber} for which the signals are to be offloaded.
     * @param <T> Type of items received by the passed and returned {@link Subscriber}.
     * @return New {@link Subscriber} that will offload signals to the passed {@link Subscriber}.
     */
    <T> Subscriber<? super T> offloadSubscription(Subscriber<? super T> subscriber);

    /**
     * Decorates the passed {@link Single.Subscriber} such that all method calls to its {@link Cancellable} will be
     * offloaded.
     * <em>None of the {@link Single.Subscriber} methods will be offloaded.</em>
     * <h2>Caution</h2>
     * This method MUST not be called concurrently with itself or other offload methods here on the same
     * {@link SignalOffloader} instance.
     *
     * @param subscriber {@link Single.Subscriber} for which the signals are to be offloaded.
     * @param <T> Type of items received by the passed and returned {@link Single.Subscriber}.
     * @return New {@link Single.Subscriber} that will offload signals to the passed {@link Single.Subscriber}.
     */
    <T> Single.Subscriber<? super T> offloadCancellable(Single.Subscriber<? super T> subscriber);

    /**
     * Decorates the passed {@link Completable.Subscriber} such that all method calls to its {@link Cancellable} will
     * be offloaded.
     * <em>None of the {@link Completable.Subscriber} methods will be offloaded.</em>
     * <h2>Caution</h2>LoadBalancerReadyHttpClientTest
     * This method MUST not be called concurrently with itself or other offload methods here on the same
     * {@link SignalOffloader} instance.
     *
     * @param subscriber {@link Completable.Subscriber} for which the signals are to be offloaded.
     * @return New {@link Completable.Subscriber} that will offload signals to the passed {@link Completable.Subscriber}.
     */
    Completable.Subscriber offloadCancellable(Completable.Subscriber subscriber);

    /**
     * Offloads subscribe call for the passed {@link Subscriber}.
     *
     * <h2>Caution</h2>
     * This method MUST not be called concurrently with itself or other offload methods here on the same
     * {@link SignalOffloader} instance.
     *
     * @param subscriber {@link Subscriber} for which subscribe call has to be offloaded.
     * @param handleSubscribe {@link Consumer} to handle the offloaded subscribe call.
     * @param <T> Type of signal.
     */
    <T> void offloadSubscribe(Subscriber<T> subscriber, Consumer<Subscriber<T>> handleSubscribe);

    /**
     * Offloads subscribe call for the passed {@link Subscriber}.
     *
     * <h2>Caution</h2>
     * This method MUST not be called concurrently with itself or other offload methods here on the same
     * {@link SignalOffloader} instance.
     *
     * @param subscriber {@link Single.Subscriber} for which subscribe call has to be offloaded.
     * @param handleSubscribe {@link Consumer} to handle the offloaded subscribe call.
     * @param <T> Type of signal.
     */
    <T> void offloadSubscribe(Single.Subscriber<T> subscriber, Consumer<Single.Subscriber<T>> handleSubscribe);

    /**
     * Offloads the subscribe call for the passed {@link Subscriber}.
     *
     * <h2>Caution</h2>
     * This method MUST not be called concurrently with itself or other offload methods here on the same
     * {@link SignalOffloader} instance.
     *
     * @param subscriber {@link Subscriber} for which for which subscribe call has to be offloaded.
     * @param handleSubscribe {@link Consumer} to handle the offloaded subscribe call.
     */
    void offloadSubscribe(Completable.Subscriber subscriber, Consumer<Completable.Subscriber> handleSubscribe);

    /**
     * Offloads the consumption of the passed {@code signal} by the passed {@link Consumer}.
     *
     * <h2>Caution</h2>
     * This method MUST not be called concurrently with itself or other offload methods here on the same
     * {@link SignalOffloader} instance.
     *
     * @param signal {@code signal} to send to the {@link Consumer}.
     * @param signalConsumer {@link Consumer} of the signal.
     * @param <T> Type of signal.
     */
    <T> void offloadSignal(T signal, Consumer<T> signalConsumer);

    /**
     * Determine if we are currently on the thread responsible for offloading publish signals. Publish signals are
     * offloaded when using operators like {@code publishOn}.
     * <p>
     * If this method is used to conditionally avoid offloading it may impact ordering. If your events are sensitive to
     * ordering you should use an alternative mechanism.
     *
     * @return {@code true} if we are currently on the thread responsible for offloading signals for subscribers.
     * @see #isInOffloadThreadForSubscribe()
     */
    boolean isInOffloadThreadForPublish();

    /**
     * Determine if we are currently on the thread responsible for offloading subscribe signals. Subscribe signals are
     * offloaded when using operators like {@code subscribeOn}.
     * <p>
     * If this method is used to conditionally avoid offloading it may impact ordering. If your events are sensitive to
     * ordering you should use an alternative mechanism.
     *
     * @return {@code true} if we are currently on the thread responsible for offloading subscribe signals.
     * @see #isInOffloadThreadForPublish()
     */
    boolean isInOffloadThreadForSubscribe();
}
