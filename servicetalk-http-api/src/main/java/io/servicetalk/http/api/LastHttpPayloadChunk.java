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

import io.servicetalk.buffer.api.Buffer;

/**
 * A special instance of {@link HttpPayloadChunk} that terminates the payload and contains
 * <a href="https://tools.ietf.org/html/rfc7230#section-4.4">trailer headers</a>.
 */
public interface LastHttpPayloadChunk extends HttpPayloadChunk, LastHttpMetaData {
    @Override
    LastHttpPayloadChunk duplicate();

    @Override
    LastHttpPayloadChunk replace(Buffer content);
}
