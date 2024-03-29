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

import io.servicetalk.concurrent.api.BiIntFunction;
import io.servicetalk.concurrent.api.Completable;
import io.servicetalk.concurrent.api.DeliberateException;
import io.servicetalk.concurrent.api.MockedSubscriberRule;
import io.servicetalk.concurrent.api.TestCompletable;
import io.servicetalk.concurrent.api.TestPublisher;

import org.junit.Rule;
import org.junit.Test;

import static io.servicetalk.concurrent.api.DeliberateException.DELIBERATE_EXCEPTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class RetryWhenTest {

    @Rule
    public MockedSubscriberRule<Integer> subscriberRule = new MockedSubscriberRule<>();
    private TestPublisher<Integer> source;
    private BiIntFunction<Throwable, Completable> shouldRetry;
    private TestCompletable retrySignal;

    @Test
    public void testComplete() {
        init(true);
        subscriberRule.request(2);
        source.sendItems(1, 2).onComplete();
        subscriberRule.verifySuccess(1, 2);
        verifyZeroInteractions(shouldRetry);
    }

    @Test
    public void testRetryCount() {
        init(true);
        subscriberRule.request(2);
        source.sendItems(1, 2).fail();
        subscriberRule.verifyItems(1, 2);
        DeliberateException fatal = new DeliberateException();
        retrySignal.onError(fatal); // stop retry
        subscriberRule.verifyFailure(fatal);
        verify(shouldRetry).apply(1, DELIBERATE_EXCEPTION);
        verifyNoMoreInteractions(shouldRetry);
    }

    @Test
    public void testRequestAcrossRepeat() {
        init(true);
        subscriberRule.request(3);
        source.sendItems(1, 2).fail();
        subscriberRule.verifyItems(1, 2);
        retrySignal.onComplete(); // trigger retry
        verify(shouldRetry).apply(1, DELIBERATE_EXCEPTION);
        source.verifySubscribed().sendItems(3);
        subscriberRule.verifyItems(3).verifyNoEmissions();
    }

    @Test
    public void testTwoError() {
        init(true);
        subscriberRule.request(3);
        source.sendItems(1, 2).fail();
        subscriberRule.verifyItems(1, 2).verifyNoEmissions();
        verify(shouldRetry).apply(1, DELIBERATE_EXCEPTION);
        retrySignal.onComplete(); // trigger retry
        source.verifySubscribed();
        source.sendItems(3).fail();
        verify(shouldRetry).apply(2, DELIBERATE_EXCEPTION);
        retrySignal.onComplete(); // trigger retry
        source.onComplete();
        subscriberRule.verifySuccess(1, 2, 3);
    }

    @Test
    public void testMaxRetries() {
        init(true);
        subscriberRule.request(3);
        source.sendItems(1, 2).fail();
        retrySignal.onComplete(); // trigger retry
        subscriberRule.verifyItems(1, 2).verifyNoEmissions();
        verify(shouldRetry).apply(1, DELIBERATE_EXCEPTION);
        source.verifySubscribed().fail();
        DeliberateException fatal = new DeliberateException();
        retrySignal.verifyListenCalled().onError(fatal); // stop retry
        subscriberRule.verifyFailure(fatal);
    }

    @Test
    public void testCancelPostErrorButBeforeRetryStart() {
        init(false);
        subscriberRule.request(2);
        source.sendItems(1, 2).fail();
        retrySignal.verifyListenCalled();
        subscriberRule.verifyItems(1, 2).cancel();
        retrySignal.verifyCancelled();
        source.verifyNotSubscribed();
        verify(shouldRetry).apply(1, DELIBERATE_EXCEPTION);
    }

    @Test
    public void testCancelBeforeRetry() {
        init(true);
        subscriberRule.request(2);
        source.sendItems(1, 2);
        subscriberRule.verifyItems(1, 2).cancel();
        source.onComplete();
        source.verifyCancelled();
    }

    @Test
    public void exceptionInTerminalCallsOnError() {
        init(true);
        DeliberateException ex = new DeliberateException();
        subscriberRule = new MockedSubscriberRule<>();
        source = new TestPublisher<>(true);
        source.sendOnSubscribe();
        subscriberRule.subscribe(source.retryWhen((times, cause) -> {
            throw ex;
        })).request(1);
        source.fail();
        subscriberRule.verifyFailure(ex);
        assertEquals(1, ex.getSuppressed().length);
        assertSame(DELIBERATE_EXCEPTION, ex.getSuppressed()[0]);
    }

    @Test
    public void nullInTerminalCallsOnError() {
        init(true);
        subscriberRule = new MockedSubscriberRule<>();
        source = new TestPublisher<>(true);
        source.sendOnSubscribe();
        subscriberRule.subscribe(source.retryWhen((times, cause) -> null)).request(1);
        source.fail();
        subscriberRule.verifyFailure(NullPointerException.class);
    }

    @SuppressWarnings("unchecked")
    private void init(boolean preserveSubscriber) {
        source = new TestPublisher<>(preserveSubscriber);
        source.sendOnSubscribe();
        shouldRetry = (BiIntFunction<Throwable, Completable>) mock(BiIntFunction.class);
        retrySignal = new TestCompletable();
        when(shouldRetry.apply(anyInt(), any())).thenAnswer(invocation -> {
            retrySignal = new TestCompletable();
            return retrySignal;
        });
        subscriberRule.subscribe(source.retryWhen(shouldRetry));
    }
}
