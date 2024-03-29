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

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

final class LiftSynchronousCompletableOperator extends AbstractSynchronousCompletableOperator {
    private final Function<Subscriber, Subscriber> customOperator;

    LiftSynchronousCompletableOperator(Completable original,
                                       Function<Subscriber, Subscriber> customOperator,
                                       Executor executor) {
        super(original, executor);
        this.customOperator = requireNonNull(customOperator);
    }

    @Override
    public Subscriber apply(final Subscriber subscriber) {
        return customOperator.apply(subscriber);
    }
}
