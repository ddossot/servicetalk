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
package io.servicetalk.http.router.jersey;

public class ExecutionStrategyTest extends AbstractExecutionStrategyTest {
    public ExecutionStrategyTest(final String path,
                                 final String routerExecutorSupplierId,
                                 final String methodExecutorSupplierId,
                                 final ExpectedExecutor expectedExecutor,
                                 final TestMode testMode) throws Exception {
        super(path, routerExecutorSupplierId, methodExecutorSupplierId, expectedExecutor, testMode);
    }
}
