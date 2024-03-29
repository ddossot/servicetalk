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
package io.servicetalk.concurrent.api.completable;

import io.servicetalk.concurrent.api.Completable;
import io.servicetalk.concurrent.api.DeliberateException;
import io.servicetalk.concurrent.api.MockedCompletableListenerRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.function.Supplier;

import static io.servicetalk.concurrent.api.DeliberateException.DELIBERATE_EXCEPTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public abstract class AbstractDoSubscriberTest {

    @Rule
    public final MockedCompletableListenerRule listener = new MockedCompletableListenerRule();

    private Completable.Subscriber subscriber;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        subscriber = mock(Completable.Subscriber.class);
    }

    @Test
    public void testOnWithOnComplete() {
        listener.listen(doSubscriber(Completable.completed(), () -> subscriber)).verifyCompletion();
        verify(subscriber).onSubscribe(any());
        verify(subscriber).onComplete();
    }

    @Test
    public void testOnWithOnError() {
        listener.listen(doSubscriber(Completable.error(DELIBERATE_EXCEPTION), () -> subscriber)).verifyFailure(DELIBERATE_EXCEPTION);
        verify(subscriber).onSubscribe(any());
        verify(subscriber).onError(DeliberateException.DELIBERATE_EXCEPTION);
    }

    protected abstract Completable doSubscriber(Completable completable, Supplier<Completable.Subscriber> subscriberSupplier);
}
