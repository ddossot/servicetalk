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
package io.servicetalk.http.api;

import io.servicetalk.transport.api.ConnectionContext;

import static io.servicetalk.http.api.BlockingUtils.blockingInvocation;
import static java.util.Objects.requireNonNull;

final class HttpServiceToBlockingHttpService extends BlockingHttpService {
    private final HttpService service;

    HttpServiceToBlockingHttpService(HttpService service) {
        this.service = requireNonNull(service);
    }

    @Override
    public HttpResponse<HttpPayloadChunk> handle(final ConnectionContext ctx,
                                                 final HttpRequest<HttpPayloadChunk> request)
            throws Exception {
        return blockingInvocation(service.handle(ctx, request));
    }

    @Override
    public void close() throws Exception {
        blockingInvocation(service.closeAsync());
    }

    @Override
    HttpService asServiceInternal() {
        return service;
    }
}
