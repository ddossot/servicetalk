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
package io.servicetalk.http.router.predicate;

import io.servicetalk.concurrent.api.AsyncCloseable;
import io.servicetalk.concurrent.api.Completable;
import io.servicetalk.concurrent.api.Single;
import io.servicetalk.http.api.HttpPayloadChunk;
import io.servicetalk.http.api.StreamingHttpRequest;
import io.servicetalk.http.api.StreamingHttpResponse;
import io.servicetalk.http.api.StreamingHttpService;
import io.servicetalk.transport.api.ConnectionContext;

import java.util.List;

import static io.servicetalk.concurrent.api.AsyncCloseables.newCompositeCloseable;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * An {@link StreamingHttpService} implementation which routes requests to a number of other {@link StreamingHttpService}s based on
 * predicates.
 * <p>
 * The predicates from the specified {@link PredicateServicePair}s are evaluated in order, and the service from the
 * first one which returns {@code true} is used to handle the request. If no predicates match, the fallback service
 * specified is used.
 */
final class InOrderRouter extends StreamingHttpService {

    private final StreamingHttpService fallbackService;
    private final PredicateServicePair[] predicateServicePairs;
    private final AsyncCloseable closeable;

    /**
     * Constructs a router service with the specified fallback service, and predicate-service pairs to evaluate.
     * @param fallbackService the service to use to handle requests if no predicates match.
     * @param predicateServicePairs the list of predicate-service pairs to use for handling requests.
     */
    @SuppressWarnings("unchecked")
    InOrderRouter(final StreamingHttpService fallbackService, final List<PredicateServicePair> predicateServicePairs) {
        this.fallbackService = requireNonNull(fallbackService);
        this.predicateServicePairs = predicateServicePairs.toArray(new PredicateServicePair[0]);
        this.closeable = newCompositeCloseable()
                .mergeAll(fallbackService)
                .mergeAll(predicateServicePairs.stream().map(PredicateServicePair::getService).collect(toList()));
    }

    @Override
    public Single<StreamingHttpResponse<HttpPayloadChunk>> handle(final ConnectionContext ctx,
                                                                  final StreamingHttpRequest<HttpPayloadChunk> request) {
        for (final PredicateServicePair pair : predicateServicePairs) {
            if (pair.getPredicate().test(ctx, request)) {
                return pair.getService().handle(ctx, request);
            }
        }
        return fallbackService.handle(ctx, request);
    }

    @Override
    public Completable closeAsync() {
        return closeable.closeAsync();
    }

    @Override
    public Completable closeAsyncGracefully() {
        return closeable.closeAsyncGracefully();
    }
}
