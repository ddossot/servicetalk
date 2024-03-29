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

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

import static java.util.Objects.requireNonNull;

final class ContextPreservingCompletableObserver implements CompletableObserver {
    private final AsyncContextMap saved;
    private final CompletableObserver actual;

    ContextPreservingCompletableObserver(AsyncContextMap saved, CompletableObserver actual) {
        this.saved = requireNonNull(saved);
        this.actual = requireNonNull(actual);
    }

    @Override
    public void onSubscribe(Disposable d) {
        AsyncContextMap prev = AsyncContext.current();
        try {
            AsyncContext.replace(saved);
            actual.onSubscribe(d);
        } finally {
            AsyncContext.replace(prev);
        }
    }

    @Override
    public void onComplete() {
        AsyncContextMap prev = AsyncContext.current();
        try {
            AsyncContext.replace(saved);
            actual.onComplete();
        } finally {
            AsyncContext.replace(prev);
        }
    }

    @Override
    public void onError(Throwable e) {
        AsyncContextMap prev = AsyncContext.current();
        try {
            AsyncContext.replace(saved);
            actual.onError(e);
        } finally {
            AsyncContext.replace(prev);
        }
    }
}
