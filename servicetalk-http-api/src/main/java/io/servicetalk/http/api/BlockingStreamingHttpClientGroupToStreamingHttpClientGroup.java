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

import io.servicetalk.client.api.GroupKey;
import io.servicetalk.concurrent.api.Completable;
import io.servicetalk.concurrent.api.Single;
import io.servicetalk.http.api.BlockingStreamingHttpClient.UpgradableBlockingStreamingHttpResponse;
import io.servicetalk.http.api.StreamingHttpClient.ReservedStreamingHttpConnection;
import io.servicetalk.http.api.StreamingHttpClient.UpgradableStreamingHttpResponse;

import static io.servicetalk.concurrent.api.Completable.error;
import static io.servicetalk.concurrent.api.Publisher.from;
import static io.servicetalk.http.api.BlockingUtils.blockingToCompletable;
import static io.servicetalk.http.api.BlockingUtils.blockingToSingle;
import static io.servicetalk.http.api.StreamingHttpResponses.fromBlockingResponse;
import static java.util.Objects.requireNonNull;

final class BlockingStreamingHttpClientGroupToStreamingHttpClientGroup<UnresolvedAddress> extends StreamingHttpClientGroup<UnresolvedAddress> {
    private final BlockingStreamingHttpClientGroup<UnresolvedAddress> blockingClientGroup;

    BlockingStreamingHttpClientGroupToStreamingHttpClientGroup(BlockingStreamingHttpClientGroup<UnresolvedAddress> blockingClientGroup) {
        this.blockingClientGroup = requireNonNull(blockingClientGroup);
    }

    @Override
    public Single<StreamingHttpResponse<HttpPayloadChunk>> request(final GroupKey<UnresolvedAddress> key,
                                                                   final StreamingHttpRequest<HttpPayloadChunk> request) {
        return blockingToSingle(() -> fromBlockingResponse(blockingClientGroup.request(key,
                new DefaultBlockingStreamingHttpRequest<>(request))));
    }

    @Override
    public Single<? extends ReservedStreamingHttpConnection> reserveConnection(
            final GroupKey<UnresolvedAddress> key, final StreamingHttpRequest<HttpPayloadChunk> request) {
        return blockingToSingle(() -> new BlockingStreamingHttpClientToStreamingHttpClient.BlockingToReservedStreamingHttpConnection(blockingClientGroup.reserveConnection(
                key, new DefaultBlockingStreamingHttpRequest<>(request))));
    }

    @Override
    public Single<? extends UpgradableStreamingHttpResponse<HttpPayloadChunk>> upgradeConnection(
            final GroupKey<UnresolvedAddress> key, final StreamingHttpRequest<HttpPayloadChunk> request) {
        return blockingToSingle(() -> {
            UpgradableBlockingStreamingHttpResponse<HttpPayloadChunk> upgradeResponse =
                    blockingClientGroup.upgradeConnection(key, new DefaultBlockingStreamingHttpRequest<>(request));
            return new BlockingStreamingHttpClientToStreamingHttpClient.BlockingToUpgradableStreamingHttpResponse<>(upgradeResponse, from(upgradeResponse.getPayloadBody()));
        });
    }

    @Override
    public Completable onClose() {
        if (blockingClientGroup instanceof StremaingHttpClientGroupToBlockingStreamingHttpClientGroup) {
            return ((StremaingHttpClientGroupToBlockingStreamingHttpClientGroup<?>) blockingClientGroup).onClose();
        }

        return error(new UnsupportedOperationException("unsupported type: " + blockingClientGroup.getClass()));
    }

    @Override
    public Completable closeAsync() {
        return blockingToCompletable(blockingClientGroup::close);
    }

    @Override
    BlockingStreamingHttpClientGroup<UnresolvedAddress> asBlockingStreamingClientGroupInternal() {
        return blockingClientGroup;
    }
}
