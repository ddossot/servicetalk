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

import io.servicetalk.concurrent.api.AsyncCloseable;
import io.servicetalk.concurrent.api.Completable;
import io.servicetalk.concurrent.api.Single;
import io.servicetalk.transport.api.ConnectionContext;

import java.util.function.BiFunction;

/**
 * A service contract for the HTTP protocol.
 */
public abstract class StreamingHttpService implements AsyncCloseable {
    /**
     * Handles a single HTTP request.
     *
     * @param ctx Context of the service.
     * @param request to handle.
     * @return {@link Single} of HTTP response.
     */
    public abstract Single<StreamingHttpResponse<HttpPayloadChunk>> handle(
            ConnectionContext ctx, StreamingHttpRequest<HttpPayloadChunk> request);

    /**
     * Closes this {@link StreamingHttpService} asynchronously.
     *
     * @return {@link Completable} that when subscribed will close this {@link StreamingHttpService}.
     */
    @Override
    public Completable closeAsync() {
        return Completable.completed();
    }

    /**
     * Convert this {@link StreamingHttpService} to the {@link HttpService} API.
     *
     * @return a {@link HttpService} representation of this {@link StreamingHttpService}.
     */
    public final HttpService asService() {
        return asServiceInternal();
    }

    /**
     * Convert this {@link StreamingHttpService} to the {@link BlockingStreamingHttpService} API.
     * <p>
     * This API is provided for convenience for a more familiar sequential programming model. It is recommended that
     * filters are implemented using the {@link StreamingHttpService} asynchronous API for maximum portability.
     * @return a {@link BlockingStreamingHttpService} representation of this {@link StreamingHttpService}.
     */
    public final BlockingStreamingHttpService asBlockingStreamingService() {
        return asBlockingStreamingServiceInternal();
    }

    /**
     * Convert this {@link StreamingHttpService} to the {@link BlockingHttpService} API.
     * <p>
     * This API is provided for convenience for a more familiar sequential programming model. It is recommended that
     * filters are implemented using the {@link StreamingHttpService} asynchronous API for maximum portability.
     * @return a {@link BlockingHttpService} representation of this {@link StreamingHttpService}.
     */
    public final BlockingHttpService asBlockingService() {
        return asBlockingServiceInternal();
    }

    /**
     * Create a new {@link StreamingHttpService} from a {@link BiFunction}.
     * @param handleFunc Provides the functionality for the {@link #handle(ConnectionContext, StreamingHttpRequest)}
     * method.
     * @return a new {@link StreamingHttpService}.
     */
    public static StreamingHttpService from(
            BiFunction<ConnectionContext, StreamingHttpRequest<HttpPayloadChunk>,
                                    Single<StreamingHttpResponse<HttpPayloadChunk>>> handleFunc) {
        return new StreamingHttpService() {
            @Override
            public Single<StreamingHttpResponse<HttpPayloadChunk>> handle(
                    final ConnectionContext ctx, final StreamingHttpRequest<HttpPayloadChunk> request) {
                return handleFunc.apply(ctx, request);
            }
        };
    }

    HttpService asServiceInternal() {
        return new StreamingHttpServiceToHttpService(this);
    }

    BlockingStreamingHttpService asBlockingStreamingServiceInternal() {
        return new StreamingHttpServiceToBlockingStreamingHttpService(this);
    }

    BlockingHttpService asBlockingServiceInternal() {
        return new StreamingHttpServiceToBlockingHttpService(this);
    }
}
