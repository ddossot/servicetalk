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
package io.servicetalk.rxjava.context;

import io.servicetalk.concurrent.context.AsyncContext;
import io.servicetalk.concurrent.context.AsyncContextMap;

import org.reactivestreams.Subscription;

import static java.util.Objects.requireNonNull;

final class ContextPreservingSubscription implements Subscription {
    private final AsyncContextMap saved;
    private final Subscription actual;

    ContextPreservingSubscription(AsyncContextMap saved, Subscription actual) {
        this.saved = requireNonNull(saved);
        this.actual = requireNonNull(actual);
    }

    @Override
    public void request(long l) {
        AsyncContextMap prev = AsyncContext.current();
        try {
            AsyncContext.replace(saved);
            actual.request(l);
        } finally {
            AsyncContext.replace(prev);
        }
    }

    @Override
    public void cancel() {
        AsyncContextMap prev = AsyncContext.current();
        try {
            AsyncContext.replace(saved);
            actual.cancel();
        } finally {
            AsyncContext.replace(prev);
        }
    }
}
