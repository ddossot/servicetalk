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

import org.junit.Test;

import static io.servicetalk.concurrent.api.DeliberateException.DELIBERATE_EXCEPTION;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

public class DoAfterFinallyTest extends AbstractDoFinallyTest {
    @Override
    protected Completable doFinally(Completable completable, Runnable runnable) {
        return completable.doAfterFinally(runnable);
    }

    @Test
    @Override
    public void testCallbackThrowsErrorOnComplete() {
        thrown.expect(is(sameInstance(DELIBERATE_EXCEPTION)));

        try {
            listener.listen(doFinally(Completable.completed(), () -> {
                throw DELIBERATE_EXCEPTION;
            }));
            fail();
        } finally {
            listener.verifyCompletion();
        }
    }

    @Test
    @Override
    public void testCallbackThrowsErrorOnError() {
        thrown.expect(is(sameInstance(DELIBERATE_EXCEPTION)));

        DeliberateException exception = new DeliberateException();
        try {
            listener.listen(doFinally(Completable.error(exception), () -> {
                throw DELIBERATE_EXCEPTION;
            }));
            fail();
        } finally {
            listener.verifyFailure(exception);
        }
    }
}
