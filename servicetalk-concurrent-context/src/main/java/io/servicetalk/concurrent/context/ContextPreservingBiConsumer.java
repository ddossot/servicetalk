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
package io.servicetalk.concurrent.context;

import java.util.function.BiConsumer;

import static io.servicetalk.concurrent.context.DefaultAsyncContextProvider.INSTANCE;
import static java.util.Objects.requireNonNull;

final class ContextPreservingBiConsumer<T, U> implements BiConsumer<T, U> {
    private final AsyncContextMap saved;
    private final BiConsumer<T, U> delegate;

    ContextPreservingBiConsumer(BiConsumer<T, U> delegate) {
        this.saved = INSTANCE.getContextMap();
        this.delegate = delegate instanceof ContextPreservingBiConsumer ?
                ((ContextPreservingBiConsumer<T, U>) delegate).delegate : requireNonNull(delegate);
    }

    @Override
    public void accept(T t, U u) {
        AsyncContextMap prev = INSTANCE.getContextMap();
        try {
            INSTANCE.setContextMap(saved);
            delegate.accept(t, u);
        } finally {
            INSTANCE.setContextMap(prev);
        }
    }
}
