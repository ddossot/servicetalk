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
package io.servicetalk.client.internal;

import io.servicetalk.concurrent.api.Completable;
import io.servicetalk.concurrent.api.Publisher;
import io.servicetalk.concurrent.api.PublisherRule;
import io.servicetalk.concurrent.internal.ServiceTalkTestTimeout;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static io.servicetalk.concurrent.api.Completable.completed;
import static io.servicetalk.concurrent.api.Completable.never;
import static io.servicetalk.concurrent.api.Publisher.just;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractRequestConcurrencyControllerOnlySingleTest {
    @Rule
    public final Timeout timeout = new ServiceTalkTestTimeout();
    @Rule
    public final PublisherRule<Integer> limitRule = new PublisherRule<>();

    protected abstract RequestConcurrencyController newController(Publisher<Integer> maxSetting, Completable onClose);

    @Test
    public void singleRequestAtTime() {
        RequestConcurrencyController controller = newController(just(1), never());
        for (int i = 0; i < 100; ++i) {
            assertTrue(controller.tryRequest());
            assertFalse(controller.tryRequest());
            controller.requestFinished();
        }
    }

    @Test
    public void singleRequestEventIfLimitIsHigher() {
        RequestConcurrencyController controller = newController(limitRule.getPublisher(), never());
        for (int i = 1; i < 100; ++i) {
            limitRule.sendItems(i);
            assertTrue(controller.tryRequest());
            assertFalse(controller.tryRequest());
            controller.requestFinished();
        }
    }

    @Test
    public void singleRequestEventIfLimitIsLower() {
        RequestConcurrencyController controller = newController(limitRule.getPublisher(), never());
        limitRule.sendItems(0);
        assertFalse(controller.tryRequest());

        limitRule.sendItems(1);
        assertTrue(controller.tryRequest());
        assertFalse(controller.tryRequest());

        limitRule.sendItems(0);
        controller.requestFinished();

        assertFalse(controller.tryRequest());

        limitRule.sendItems(1);
        assertTrue(controller.tryRequest());
        assertFalse(controller.tryRequest());
    }

    @Test
    public void noMoreRequestsAfterClose() {
        RequestConcurrencyController controller = newController(just(1), completed());
        assertFalse(controller.tryRequest());
    }
}
