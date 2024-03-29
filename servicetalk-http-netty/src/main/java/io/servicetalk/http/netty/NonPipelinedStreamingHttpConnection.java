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
package io.servicetalk.http.netty;

import io.servicetalk.concurrent.api.Publisher;
import io.servicetalk.transport.api.ExecutionContext;
import io.servicetalk.transport.netty.internal.Connection;

final class NonPipelinedStreamingHttpConnection extends AbstractStreamingHttpConnection<Connection<Object, Object>> {

    NonPipelinedStreamingHttpConnection(Connection<Object, Object> connection, ReadOnlyHttpClientConfig config,
                                        ExecutionContext executionContext) {
        super(connection, connection.onClosing(), config, executionContext);
    }

    @Override
    protected Publisher<Object> writeAndRead(final Publisher<Object> requestStream) {
        return connection.write(requestStream).andThen(connection.read());
    }
}
