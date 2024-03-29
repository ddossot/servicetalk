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
package io.servicetalk.concurrent.api.single;

import io.servicetalk.concurrent.api.MockedSingleListenerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.TimeoutException;

import static io.servicetalk.concurrent.api.DeliberateException.DELIBERATE_EXCEPTION;
import static io.servicetalk.concurrent.api.Single.error;
import static io.servicetalk.concurrent.api.Single.never;
import static io.servicetalk.concurrent.api.Single.success;
import static io.servicetalk.concurrent.internal.Await.await;
import static io.servicetalk.concurrent.internal.Await.awaitIndefinitely;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class AwaitTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Rule
    public final MockedSingleListenerRule rule = new MockedSingleListenerRule();

    @Test
    public void testFailure() throws Throwable {
        thrown.expectCause(is(DELIBERATE_EXCEPTION));
        awaitIndefinitely(error(DELIBERATE_EXCEPTION));
    }

    @Test
    public void testSuccess() throws Throwable {
        assertThat("Unexpected result.", awaitIndefinitely(success("Hello")), is("Hello"));
    }

    @Test
    public void testSuccessAwaitTimeout() throws Throwable {
        assertThat("Unexpected result.", await(success("Hello"), 1, MINUTES), is("Hello"));
    }

    @Test
    public void testFailureAwaitTimeout() throws Throwable {
        thrown.expectCause(is(DELIBERATE_EXCEPTION));
        assertThat("Unexpected result.", await(error(DELIBERATE_EXCEPTION), 1, MINUTES), is("Hello"));
    }

    @Test
    public void testTimeout() throws Throwable {
        thrown.expect(instanceOf(TimeoutException.class));
        await(never(), 1, MILLISECONDS);
    }
}
