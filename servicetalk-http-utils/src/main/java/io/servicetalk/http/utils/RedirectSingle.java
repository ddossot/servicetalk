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
package io.servicetalk.http.utils;

import io.servicetalk.concurrent.Cancellable;
import io.servicetalk.concurrent.api.Single;
import io.servicetalk.concurrent.internal.SequentialCancellable;
import io.servicetalk.http.api.HttpHeaders;
import io.servicetalk.http.api.HttpPayloadChunk;
import io.servicetalk.http.api.HttpRequestMethod;
import io.servicetalk.http.api.StreamingHttpClientGroup;
import io.servicetalk.http.api.StreamingHttpRequest;
import io.servicetalk.http.api.StreamingHttpRequester;
import io.servicetalk.http.api.StreamingHttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import static io.servicetalk.http.api.HttpHeaderNames.CONTENT_LENGTH;
import static io.servicetalk.http.api.HttpHeaderNames.HOST;
import static io.servicetalk.http.api.HttpHeaderNames.LOCATION;
import static io.servicetalk.http.api.HttpHeaderValues.ZERO;
import static io.servicetalk.http.api.HttpRequestMethods.CONNECT;
import static io.servicetalk.http.api.HttpRequestMethods.GET;
import static io.servicetalk.http.api.HttpRequestMethods.HEAD;
import static io.servicetalk.http.api.HttpRequestMethods.OPTIONS;
import static io.servicetalk.http.api.HttpRequestMethods.TRACE;
import static io.servicetalk.http.api.StreamingHttpRequests.newRequest;
import static java.util.Objects.requireNonNull;

/**
 * An operator, which implements redirect logic for {@link StreamingHttpClientGroup}.
 */
final class RedirectSingle extends Single<StreamingHttpResponse<HttpPayloadChunk>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectSingle.class);

    private final Single<StreamingHttpResponse<HttpPayloadChunk>> originalResponse;
    private final StreamingHttpRequest<HttpPayloadChunk> originalRequest;
    private final int maxRedirects;
    private final StreamingHttpRequester requester;

    /**
     * Create a new {@link Single}<{@link StreamingHttpResponse}> which will be able to handle redirects.
     *
     * @param originalResponse The original {@link Single}<{@link StreamingHttpResponse}> for which redirect should be applied.
     * @param originalRequest The original {@link StreamingHttpRequest} which was sent.
     * @param maxRedirects The maximum number of follow up redirects.
     * @param requester The {@link StreamingHttpRequester} to send redirected requests, must be backed by
     * {@link StreamingHttpClientGroup}.
     */
    RedirectSingle(final Single<StreamingHttpResponse<HttpPayloadChunk>> originalResponse,
                   final StreamingHttpRequest<HttpPayloadChunk> originalRequest,
                   final int maxRedirects,
                   final StreamingHttpRequester requester) {
        this.originalResponse = requireNonNull(originalResponse);
        this.originalRequest = requireNonNull(originalRequest);
        this.maxRedirects = maxRedirects;
        this.requester = requireNonNull(requester);
    }

    @Override
    protected void handleSubscribe(final Subscriber<? super StreamingHttpResponse<HttpPayloadChunk>> subscriber) {
        originalResponse.subscribe(new RedirectSubscriber(subscriber, this, originalRequest));
    }

    private static final class RedirectSubscriber implements Subscriber<StreamingHttpResponse<HttpPayloadChunk>> {

        private final Subscriber<? super StreamingHttpResponse<HttpPayloadChunk>> target;
        private final RedirectSingle redirectSingle;
        private final StreamingHttpRequest<HttpPayloadChunk> request;
        private final int redirectCount;
        private final SequentialCancellable sequentialCancellable;

        RedirectSubscriber(final Subscriber<? super StreamingHttpResponse<HttpPayloadChunk>> target,
                           final RedirectSingle redirectSingle,
                           final StreamingHttpRequest<HttpPayloadChunk> request) {
            this(target, redirectSingle, request, 0, new SequentialCancellable());
        }

        RedirectSubscriber(final Subscriber<? super StreamingHttpResponse<HttpPayloadChunk>> target,
                           final RedirectSingle redirectSingle,
                           final StreamingHttpRequest<HttpPayloadChunk> request,
                           final int redirectCount,
                           final SequentialCancellable sequentialCancellable) {
            this.target = target;
            this.redirectSingle = redirectSingle;
            this.request = request;
            this.redirectCount = redirectCount;
            this.sequentialCancellable = sequentialCancellable;
        }

        @Override
        public void onSubscribe(final Cancellable cancellable) {
            sequentialCancellable.setNextCancellable(cancellable);
            if (redirectCount == 0) {
                target.onSubscribe(sequentialCancellable);
            }
        }

        @Override
        public void onSuccess(@Nullable final StreamingHttpResponse<HttpPayloadChunk> result) {
            if (result == null || !shouldRedirect(redirectCount + 1, result, request.getMethod())) {
                target.onSuccess(result);
                return;
            }

            final StreamingHttpRequest<HttpPayloadChunk> newRequest;
            try {
                newRequest = prepareRedirectRequest(request, result);
            } catch (final Throwable cause) {
                target.onError(cause);
                return;
            }
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Execute redirect to '{}' for original request '{}'",
                        result.getHeaders().get(LOCATION), redirectSingle.originalRequest);
            }
            // Consume any payload of the redirect response
            result.getPayloadBody().ignoreElements().subscribe();
            redirectSingle.requester.request(newRequest).subscribe(new RedirectSubscriber(
                    target, redirectSingle, newRequest, redirectCount + 1, sequentialCancellable));
        }

        @Override
        public void onError(final Throwable t) {
            target.onError(t);
        }

        private boolean shouldRedirect(final int redirectCount, final StreamingHttpResponse<HttpPayloadChunk> response,
                                       final HttpRequestMethod originalMethod) {
            final int statusCode = response.getStatus().getCode();

            if (statusCode < 300 || statusCode > 308) {
                // We start without support for status codes outside of this range
                // re-visit when we need to support payloads in redirects.
                return false;
            }

            if (redirectCount > redirectSingle.maxRedirects) {
                LOGGER.debug("Maximum number of redirects ({}) reached for original request '{}'",
                        redirectSingle.maxRedirects, redirectSingle.originalRequest);
                return false;
            }

            switch (statusCode) {
                case 304:
                    // https://tools.ietf.org/html/rfc7232#section-4.1
                    // The 304 (Not Modified) status code indicates that the cache value is still fresh and can be used.
                case 305:
                    // https://tools.ietf.org/html/rfc7231#section-6.4.5
                    // The 305 (Use Proxy) status code has been deprecated due to
                    // security concerns regarding in-band configuration of a proxy.
                case 306:
                    // https://tools.ietf.org/html/rfc7231#section-6.4.6
                    // The 306 (Unused) status code is no longer used, and the code is reserved.
                    return false;
                default:
                    // Server should return only 200 status code for TRACE.
                    // We don't see a clear use case for redirect for OPTIONS and CONNECT methods
                    // and will support them later if necessary.
                    if (originalMethod == TRACE || originalMethod == OPTIONS || originalMethod == CONNECT) {
                        return false;
                    }

                    final CharSequence locationHeader = response.getHeaders().get(LOCATION);
                    if (locationHeader == null || locationHeader.length() == 0) {
                        LOGGER.debug("No location header for redirect response");
                        return false;
                    }

                    if (statusCode == 307 || statusCode == 308) {
                        // TODO: remove these cases when we will support repeatable payload
                        return originalMethod == GET || originalMethod == HEAD;
                    }

                    return true;
            }
        }

        private static StreamingHttpRequest<HttpPayloadChunk> prepareRedirectRequest(final StreamingHttpRequest<HttpPayloadChunk> request,
                                                                                     final StreamingHttpResponse<HttpPayloadChunk> response) {
            final HttpRequestMethod method = defineRedirectMethod(request.getMethod());
            final CharSequence locationHeader = response.getHeaders().get(LOCATION);
            assert locationHeader != null;

            final StreamingHttpRequest<HttpPayloadChunk> redirectRequest =
                    newRequest(request.getVersion(), method, locationHeader.toString());

            final HttpHeaders headers = redirectRequest.getHeaders();
            // TODO CONTENT_LENGTH could be non ZERO, when we will support repeatable payloadBody
            headers.set(CONTENT_LENGTH, ZERO);

            String redirectHost = redirectRequest.getEffectiveHost();
            if (redirectHost == null) {
                // origin-form request-target in Location header, extract host & port info from original request
                redirectHost = request.getEffectiveHost();
                if (redirectHost == null) {
                    // Should never happen, otherwise the original request had to fail
                    throw new InvalidRedirectException("No host information for redirect");
                }
                final int redirectPort = request.getEffectivePort();
                headers.set(HOST, redirectPort < 0 ? redirectHost : redirectHost + ':' + redirectPort);
            }

            // NOTE: for security reasons we do not keep any headers from original request.
            // If users need to add some custom or authentication headers, they have to apply them via filters.
            return redirectRequest;
        }

        private static HttpRequestMethod defineRedirectMethod(final HttpRequestMethod originalMethod) {
            // https://tools.ietf.org/html/rfc7231#section-6.4.2
            // https://tools.ietf.org/html/rfc7231#section-6.4.3
            // Note for 301 (Moved Permanently) and 302 (Found):
            //     For historical reasons, a user agent MAY change the request method from POST to GET for the
            //     subsequent request.  If this behavior is undesired, the 307 (Temporary Redirect) or
            //     308 (Permanent Redirect) status codes can be used instead.
            // ServiceTalk follows this historical approach.

            // For 303 (See Other) user agent should perform only a
            // GET or HEAD request: https://tools.ietf.org/html/rfc7231#section-6.4.4
            return originalMethod == HEAD ? HEAD : GET;
            // TODO: It also could be originalMethod for 307 & 308, when we will support repeatable payloadBody
        }
    }
}
