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
package io.servicetalk.concurrent.api.publisher;

import io.servicetalk.concurrent.api.Executor;
import io.servicetalk.concurrent.api.Publisher;
import io.servicetalk.concurrent.api.TestIterableToBlockingIterable;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static io.servicetalk.concurrent.api.DeliberateException.DELIBERATE_EXCEPTION;
import static io.servicetalk.concurrent.api.Executors.immediate;
import static io.servicetalk.concurrent.api.Publisher.from;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

public class FromBlockingIterableTest extends FromInMemoryPublisherAbstractTest {
    @Override
    protected InMemorySource newPublisher(final Executor executor, final String[] values) {
        return newPublisher(executor, values, (timeout, unit) -> { }, (timeout, unit) -> { }, () -> { });
    }

    private InMemorySource newPublisher(final Executor executor, final String[] values,
                                        BiConsumer<Long, TimeUnit> hashNextConsumer,
                                        BiConsumer<Long, TimeUnit> nextConsumer,
                                        AutoCloseable closeable) {
        return new InMemorySource(values) {
            private final Publisher<String> publisher = from(
                    new TestIterableToBlockingIterable<>(asList(values), hashNextConsumer, nextConsumer,
                            closeable), () -> 1, SECONDS);
            @Override
            protected Publisher<String> getPublisher() {
                return publisher;
            }
        };
    }

    private InMemorySource newSource(int size, BiConsumer<Long, TimeUnit> hashNextConsumer,
                                     BiConsumer<Long, TimeUnit> nextConsumer,
                                     AutoCloseable closeable) {
        String[] values = new String[size];
        for (int i = 0; i < size; i++) {
            values[i] = "Hello" + i;
        }
        return newPublisher(immediate(), values, hashNextConsumer, nextConsumer, closeable);
    }

    @Test
    public void requestNTimeoutIsCaught() {
        BiConsumer<Long, TimeUnit> hashNextConsumer = (timeout, unit) -> {
            throw DELIBERATE_EXCEPTION;
        };
        BiConsumer<Long, TimeUnit> nextConsumer = (timeout, unit) -> {
            throw new IllegalStateException("should not be called!");
        };
        AtomicBoolean cancelled = new AtomicBoolean();
        InMemorySource source = newSource(1, hashNextConsumer, nextConsumer, () -> cancelled.set(true));
        subscriber.subscribe(source.getPublisher()).request(1)
                .verifyFailure(DELIBERATE_EXCEPTION).verifyNoEmissions();
        assertTrue(cancelled.get());
    }

    @Test
    public void nextWithTimeoutIsCalled() {
        BiConsumer<Long, TimeUnit> hashNextConsumer = (timeout, unit) -> {
        };
        BiConsumer<Long, TimeUnit> nextConsumer = (timeout, unit) -> {
            throw DELIBERATE_EXCEPTION;
        };
        AtomicBoolean cancelled = new AtomicBoolean();
        InMemorySource source = newSource(1, hashNextConsumer, nextConsumer, () -> cancelled.set(true));
        subscriber.subscribe(source.getPublisher()).request(1)
                .verifyFailure(DELIBERATE_EXCEPTION).verifyNoEmissions();
        assertTrue(cancelled.get());
    }
}
