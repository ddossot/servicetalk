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

import static io.servicetalk.concurrent.api.Executors.immediate;
import static io.servicetalk.concurrent.api.MergedExecutors.mergeAndOffloadPublish;
import static io.servicetalk.concurrent.api.MergedExecutors.mergeAndOffloadSubscribe;

/**
 * A set of factory methods that provides implementations for the various publish/subscribeOn methods on
 * {@link Completable}.
 */
final class PublishAndSubscribeOnCompletables {

    private PublishAndSubscribeOnCompletables() {
        // No instance.
    }

    static Completable publishAndSubscribeOn(Completable original, Executor executor) {
        return original.getExecutor() == executor || executor == immediate() ?
                original : new PublishAndSubscribeOn(executor, original);
    }

    static Completable publishAndSubscribeOnOverride(Completable original, Executor executor) {
        return original.getExecutor() == executor || executor == immediate() ?
                original : new PublishAndSubscribeOnOverride(original, executor);
    }

    static Completable publishOn(Completable original, Executor executor) {
        return original.getExecutor() == executor || executor == immediate() ?
                original : new PublishOn(executor, original);
    }

    static Completable publishOnOverride(Completable original, Executor executor) {
        return original.getExecutor() == executor || executor == immediate() ?
                original : new PublishOnOverride(original, executor);
    }

    static Completable subscribeOn(Completable original, Executor executor) {
        return original.getExecutor() == executor || executor == immediate() ?
                original : new SubscribeOn(executor, original);
    }

    static Completable subscribeOnOverride(Completable original, Executor executor) {
        return original.getExecutor() == executor || executor == immediate() ?
                original : new SubscribeOnOverride(original, executor);
    }

    private static final class PublishAndSubscribeOn extends AbstractNoHandleSubscribeCompletable {
        private final Completable original;

        PublishAndSubscribeOn(final Executor executor, final Completable original) {
            super(executor);
            this.original = original;
        }

        @Override
        void handleSubscribe(final Subscriber subscriber, final SignalOffloader signalOffloader) {
            // This operator is to make sure that we use the executor to subscribe to the Completable that is returned
            // by this operator.
            //
            // Here we offload signals from original to subscriber using signalOffloader.
            // We use executor to create the returned Completable which means executor will be used
            // to offload handleSubscribe as well as the Subscription that is sent to the subscriber here.
            //
            // This operator acts as a boundary that changes the Executor from original to the rest of the execution
            // chain. If there is already an Executor defined for original, it will be used to offload signals until
            // they hit this operator.
            original.subscribe(signalOffloader.offloadSubscriber(subscriber));
        }
    }

    /**
     * This operator is to make sure that we override the {@link Executor} for the entire execution chain. This is the
     * normal mode of operation if we create a {@link Completable} with an {@link Executor}, i.e. all operators behave
     * the same way. Hence, we simply use {@link AbstractSynchronousCompletableOperator} which does not do any extra
     * offloading, it just overrides the {@link Executor} that will be used to do the offloading.
     */
    private static final class PublishAndSubscribeOnOverride extends AbstractSynchronousCompletableOperator {
        PublishAndSubscribeOnOverride(final Completable original, final Executor executor) {
            super(original, executor);
        }

        @Override
        public Subscriber apply(final Subscriber subscriber) {
            // We are using AbstractSynchronousCompletableOperator just to override the Executor. We do not intend
            // to do any extra offloading that is done by a regular Completable created with an Executor.
            return subscriber;
        }
    }

    private static class PublishOn extends AbstractNoHandleSubscribeCompletable {
        private final Completable original;

        PublishOn(final Executor executor, final Completable original) {
            super(mergeAndOffloadPublish(original.getExecutor(), executor));
            this.original = original;
        }

        @Override
        void handleSubscribe(final Subscriber subscriber, final SignalOffloader signalOffloader) {
            // This operator is to make sure that we use the executor to subscribe to the Completable that is returned
            // by this operator.
            //
            // Here we offload signals from original to subscriber using signalOffloader.
            //
            // This operator acts as a boundary that changes the Executor from original to the rest of the execution
            // chain. If there is already an Executor defined for original, it will be used to offload signals until
            // they hit this operator.
            original.subscribe(signalOffloader.offloadSubscriber(subscriber));
        }
    }

    /**
     * This operator is to make sure that we override the {@link Executor} for the entire execution chain. This is the
     * normal mode of operation if we create a {@link Completable} with an {@link Executor}, i.e. all operators behave
     * the same way.
     * Hence, we simply use {@link AbstractSynchronousCompletableOperator} which does not do any extra offloading, it
     * just overrides the {@link Executor} that will be used to do the offloading.
     */
    private static class PublishOnOverride extends AbstractSynchronousCompletableOperator {

        PublishOnOverride(final Completable original, final Executor executor) {
            super(original, mergeAndOffloadPublish(original.getExecutor(), executor));
        }

        @Override
        public Subscriber apply(final Subscriber subscriber) {
            // We are using AbstractSynchronousCompletableOperator just to override the Executor. We do not intend to
            // do any extra offloading that is done by a regular Completable created with an Executor.
            return subscriber;
        }
    }

    private static class SubscribeOn extends AbstractNoHandleSubscribeCompletable {
        private final Completable original;

        SubscribeOn(final Executor executor, final Completable original) {
            super(mergeAndOffloadSubscribe(original.getExecutor(), executor));
            this.original = original;
        }

        @Override
        void handleSubscribe(final Subscriber subscriber, final SignalOffloader signalOffloader) {
            // This operator is to make sure that we use the executor to subscribe to the Completable that is returned
            // by this operator.
            //
            // Subscription and handleSubscribe are offloaded at subscribe so we do not need to do anything specific
            // here.
            //
            // This operator acts as a boundary that changes the Executor from original to the rest of the execution
            // chain. If there is already an Executor defined for original, it will be used to offload signals until
            // they hit this operator.
            original.subscribe(subscriber);
        }
    }

    /**
     * This operator is to make sure that we override the {@link Executor} for the entire execution chain. This is the
     * normal mode of operation if we create a {@link Completable} with an {@link Executor}, i.e. all operators behave
     * the same way.
     * Hence, we simply use {@link AbstractSynchronousCompletableOperator} which does not do any extra offloading, it
     * just overrides the {@link Executor} that will be used to do the offloading.
     */
    private static class SubscribeOnOverride extends AbstractSynchronousCompletableOperator {

        SubscribeOnOverride(final Completable original, final Executor executor) {
            super(original, mergeAndOffloadSubscribe(original.getExecutor(), executor));
        }

        @Override
        public Subscriber apply(final Subscriber subscriber) {
            // We are using AbstractSynchronousCompletableOperator just to override the Executor. We do not intend to
            // do any extra offloading that is done by a regular Completable created with an Executor.
            return subscriber;
        }
    }
}
