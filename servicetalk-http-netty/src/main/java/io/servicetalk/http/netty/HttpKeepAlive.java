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

import io.servicetalk.http.api.HttpMetaData;
import io.servicetalk.http.api.HttpPayloadChunk;
import io.servicetalk.http.api.StreamingHttpResponse;

import static io.servicetalk.http.api.HttpHeaderNames.CONNECTION;
import static io.servicetalk.http.api.HttpHeaderValues.CLOSE;
import static io.servicetalk.http.api.HttpHeaderValues.KEEP_ALIVE;
import static io.servicetalk.http.api.HttpProtocolVersions.HTTP_1_0;
import static io.servicetalk.http.api.HttpProtocolVersions.HTTP_1_1;
import static io.servicetalk.http.netty.HttpProtocolVersionUtils.isSameVersion;

enum HttpKeepAlive {

    CLOSE_ADD_HEADER(true, true),
    CLOSE_NO_HEADER(true, false),
    KEEP_ALIVE_NO_HEADER(false, false),
    KEEP_ALIVE_ADD_HEADER(false, true);

    private final boolean shouldCloseConnection;
    private final boolean shouldAddConnectionHeader;

    HttpKeepAlive(final boolean shouldCloseConnection, final boolean shouldAddConnectionHeader) {
        this.shouldCloseConnection = shouldCloseConnection;
        this.shouldAddConnectionHeader = shouldAddConnectionHeader;
    }

    // In the interest of performance we are not accommodating for the spec allowing multiple header fields
    // or comma-separated values for the Connection header. See: https://tools.ietf.org/html/rfc7230#section-3.2.2
    static HttpKeepAlive getResponseKeepAlive(final HttpMetaData metaData) {
        if (isSameVersion(HTTP_1_1, metaData.getVersion())) {
            return metaData.getHeaders().contains(CONNECTION, CLOSE, true) ?
                    CLOSE_ADD_HEADER : KEEP_ALIVE_NO_HEADER;
        } else if (isSameVersion(HTTP_1_0, metaData.getVersion())) {
            return metaData.getHeaders().contains(CONNECTION, KEEP_ALIVE, true) ?
                    KEEP_ALIVE_ADD_HEADER : CLOSE_NO_HEADER;
        } else {
            return CLOSE_NO_HEADER;
        }
    }

    static boolean shouldClose(final HttpMetaData metaData) {
        return getResponseKeepAlive(metaData).shouldCloseConnection;
    }

    void addConnectionHeaderIfNecessary(final StreamingHttpResponse<HttpPayloadChunk> response) {
        if (shouldAddConnectionHeader) {
            if (shouldCloseConnection) {
                response.getHeaders().set(CONNECTION, CLOSE);
            } else {
                response.getHeaders().set(CONNECTION, KEEP_ALIVE);
            }
        }
    }
}
