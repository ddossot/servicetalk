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
package io.servicetalk.serialization.api;

import io.servicetalk.buffer.api.Buffer;
import io.servicetalk.buffer.api.BufferAllocator;
import io.servicetalk.concurrent.BlockingIterable;
import io.servicetalk.concurrent.BlockingIterator;
import io.servicetalk.concurrent.api.MockedSubscriberRule;
import io.servicetalk.concurrent.api.Publisher;
import io.servicetalk.concurrent.api.TestPublisher;
import io.servicetalk.concurrent.internal.ServiceTalkTestTimeout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.IntUnaryOperator;

import static io.servicetalk.concurrent.api.Publisher.from;
import static io.servicetalk.concurrent.internal.Await.awaitIndefinitelyNonNull;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class DefaultSerializerSerializationTest {

    private static final TypeHolder<List<String>> typeForList = new TypeHolder<List<String>>() { };

    private IntUnaryOperator sizeEstimator;
    private List<Buffer> createdBuffers;
    private StreamingSerializer serializer;
    private BufferAllocator allocator;
    private SerializationProvider provider;
    private DefaultSerializer factory;

    @Rule
    public Timeout timeout = new ServiceTalkTestTimeout();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        createdBuffers = new ArrayList<>();
        allocator = mock(BufferAllocator.class);
        when(allocator.newBuffer(anyInt())).then(invocation -> {
            Buffer b = mock(Buffer.class);
            createdBuffers.add(b);
            return b;
        });
        sizeEstimator = mock(IntUnaryOperator.class);
        when(sizeEstimator.applyAsInt(anyInt())).then(invocation -> ((Integer) invocation.getArgument(0)) + 1);
        serializer = mock(StreamingSerializer.class);
        provider = mock(SerializationProvider.class);
        when(provider.getSerializer(String.class)).thenReturn(serializer);
        when(provider.getSerializer(typeForList)).thenReturn(serializer);
        factory = new DefaultSerializer(provider);
    }

    @Test
    public void applySerializationForPublisherWithType() throws ExecutionException, InterruptedException {
        final Publisher<Buffer> serialized = factory.serialize(from("Hello1", "Hello2"), allocator, String.class);
        final List<Buffer> buffers = awaitIndefinitelyNonNull(serialized);
        verify(provider).getSerializer(String.class);
        assertThat("Unexpected created buffers.", createdBuffers, hasSize(2));
        verify(serializer).serialize("Hello1", createdBuffers.get(0));
        verify(serializer).serialize("Hello2", createdBuffers.get(1));
        assertThat("Unexpected serialized buffers.", buffers, equalTo(createdBuffers));
    }

    @Test
    public void applySerializationForPublisherWithTypeHolder() throws ExecutionException, InterruptedException {
        final List<String> first = singletonList("Hello1");
        final List<String> second = singletonList("Hello2");
        final Publisher<Buffer> serialized = factory.serialize(from(first, second), allocator, typeForList);
        final List<Buffer> buffers = awaitIndefinitelyNonNull(serialized);
        verify(provider).getSerializer(typeForList);
        assertThat("Unexpected created buffers.", createdBuffers, hasSize(2));
        verify(serializer).serialize(first, createdBuffers.get(0));
        verify(serializer).serialize(second, createdBuffers.get(1));
        assertThat("Unexpected serialized buffers.", buffers, equalTo(createdBuffers));
    }

    @Test
    public void applySerializationForPublisherWithTypeAndEstimator() {
        TestPublisher<String> source = new TestPublisher<>();
        source.sendOnSubscribe();

        final Publisher<Buffer> serialized = factory.serialize(source, allocator, String.class, sizeEstimator);
        MockedSubscriberRule<Buffer> subscriber = new MockedSubscriberRule<>();
        subscriber.subscribe(serialized).request(2);

        verify(provider).getSerializer(String.class);

        subscriber.verifyItems(verifySerializedBufferWithSizes(source, "Hello", 1));
        subscriber.verifyItems(verifySerializedBufferWithSizes(source, "Hello", 2));

        source.onComplete();
        subscriber.verifySuccess();
    }

    @Test
    public void applySerializationForPublisherWithTypeHolderAndEstimator() {
        TestPublisher<List<String>> source = new TestPublisher<>();
        source.sendOnSubscribe();

        final Publisher<Buffer> serialized = factory.serialize(source, allocator, typeForList, sizeEstimator);
        MockedSubscriberRule<Buffer> subscriber = new MockedSubscriberRule<>();
        subscriber.subscribe(serialized).request(2);

        verify(provider).getSerializer(typeForList);

        subscriber.verifyItems(verifySerializedBufferWithSizes(source, singletonList("Hello"), 1));
        subscriber.verifyItems(verifySerializedBufferWithSizes(source, singletonList("Hello"), 2));

        source.onComplete();
        subscriber.verifySuccess();
    }

    @Test
    public void applySerializationForIterableWithType() {
        final Iterable<Buffer> buffers = factory.serialize(asList("Hello1", "Hello2"), allocator, String.class);
        verify(provider).getSerializer(String.class);
        assertThat("Unexpected created buffers.", createdBuffers, hasSize(2));
        verify(serializer).serialize("Hello1", createdBuffers.get(0));
        verify(serializer).serialize("Hello2", createdBuffers.get(1));
        assertThat("Unexpected serialized buffers.", buffers, equalTo(createdBuffers));
    }

    @Test
    public void applySerializationForIterableWithTypeHolder() {
        final List<String> first = singletonList("Hello1");
        final List<String> second = singletonList("Hello2");
        final Iterable<Buffer> buffers = factory.serialize(asList(first, second), allocator, typeForList);
        verify(provider).getSerializer(typeForList);
        assertThat("Unexpected created buffers.", createdBuffers, hasSize(2));
        verify(serializer).serialize(first, createdBuffers.get(0));
        verify(serializer).serialize(second, createdBuffers.get(1));
        assertThat("Unexpected serialized buffers.", buffers, equalTo(createdBuffers));
    }

    @Test
    public void applySerializationForIterableWithTypeAndEstimator() {
        final Iterable<Buffer> buffers = factory.serialize(asList("Hello1", "Hello2"), allocator, String.class, sizeEstimator
        );
        verify(provider).getSerializer(String.class);
        assertThat("Unexpected created buffers.", createdBuffers, hasSize(2));
        verify(serializer).serialize("Hello1", createdBuffers.get(0));
        verify(serializer).serialize("Hello2", createdBuffers.get(1));
        assertThat("Unexpected serialized buffers.", buffers, equalTo(createdBuffers));
    }

    @Test
    public void applySerializationForIterableWithTypeHolderAndEstimator() {
        final List<String> first = singletonList("Hello1");
        final List<String> second = singletonList("Hello2");
        final Iterable<Buffer> buffers = factory.serialize(asList(first, second), allocator, typeForList,
                sizeEstimator);
        verify(provider).getSerializer(typeForList);
        assertThat("Unexpected created buffers.", createdBuffers, hasSize(2));
        verify(serializer).serialize(first, createdBuffers.get(0));
        verify(serializer).serialize(second, createdBuffers.get(1));
        assertThat("Unexpected serialized buffers.", buffers, equalTo(createdBuffers));
    }

    @Test
    public void applySerializationForBlockingIterableWithType() throws Exception {
        final List<String> data = asList("Hello1", "Hello2");
        BlockingIterableMock<String> source = new BlockingIterableMock<>(data);
        final BlockingIterable<Buffer> buffers = factory.serialize(source.getIterable(), allocator, String.class);
        verify(provider).getSerializer(String.class);

        drainBlockingIteratorAndVerify(data, source.getIterator(), buffers);
    }

    @Test
    public void applySerializationForBlockingIterableWithTypeHolder() throws Exception {
        final List<List<String>> data = asList(singletonList("Hello1"), singletonList("Hello2"));
        BlockingIterableMock<List<String>> source = new BlockingIterableMock<>(data);
        final BlockingIterable<Buffer> buffers = factory.serialize(source.getIterable(), allocator, typeForList);
        verify(provider).getSerializer(typeForList);

        drainBlockingIteratorAndVerify(data, source.getIterator(), buffers);
    }

    @Test
    public void applySerializationForBlockingIterableWithTypeAndEstimator() throws Exception {
        final List<String> data = asList("Hello1", "Hello2");
        BlockingIterableMock<String> source = new BlockingIterableMock<>(data);
        final BlockingIterable<Buffer> buffers = factory.serialize(source.getIterable(), allocator, String.class,
                sizeEstimator);
        verify(provider).getSerializer(String.class);

        drainBlockingIteratorAndVerify(data, source.getIterator(), buffers);
    }

    @Test
    public void applySerializationForBlockingIterableWithTypeHolderAndEstimator() throws Exception {
        final List<List<String>> data = asList(singletonList("Hello1"), singletonList("Hello2"));
        BlockingIterableMock<List<String>> source = new BlockingIterableMock<>(data);
        final BlockingIterable<Buffer> buffers = factory.serialize(source.getIterable(), allocator, typeForList,
                sizeEstimator);
        verify(provider).getSerializer(typeForList);

        drainBlockingIteratorAndVerify(data, source.getIterator(), buffers);
    }

    @Test
    public void serializeSingle() {
        final Buffer buffer = factory.serialize("Hello", allocator);
        assertThat("Unexpected created buffers.", createdBuffers, hasSize(1));
        verify(provider).serialize("Hello", createdBuffers.get(0));
        assertThat("Unexpected serialized buffers.", buffer, equalTo(createdBuffers.get(0)));
    }

    @Test
    public void serializeSingleWithSize() {
        final Buffer buffer = factory.serialize("Hello", allocator, 1);
        verify(allocator).newBuffer(1);
        assertThat("Unexpected created buffers.", createdBuffers, hasSize(1));
        verify(provider).serialize("Hello", createdBuffers.get(0));
        assertThat("Unexpected serialized buffers.", buffer, equalTo(createdBuffers.get(0)));
    }

    @Test
    public void serializeSingleWithBuffer() {
        final Buffer buffer = mock(Buffer.class);
        factory.serialize("Hello", buffer);
        verify(provider).serialize("Hello", buffer);
        verifyNoMoreInteractions(provider);
    }

    private <T> void drainBlockingIteratorAndVerify(final Iterable<T> data, final BlockingIterator<T> mockIterator,
                                                    final BlockingIterable<Buffer> buffers) throws Exception {
        final BlockingIterator<Buffer> iterator = buffers.iterator();

        int index = 0;
        for (T datum : data) {
            assertThat("Incomplete data at index: " + index, iterator.hasNext(1, TimeUnit.MILLISECONDS),
                    is(true));
            final Buffer next = iterator.next(1, TimeUnit.MILLISECONDS);
            assertThat("Unexpected created buffers.", createdBuffers, hasSize(index + 1));
            verify(serializer).serialize(datum, createdBuffers.get(index));
            assertThat("Unexpected data at index: " + index, next, is(createdBuffers.get(index)));
            index++;
        }

        iterator.close();
        verify(mockIterator).close();
    }

    private <T> Buffer verifySerializedBufferWithSizes(final TestPublisher<T> source, T item, final int sizeEstimate) {
        when(sizeEstimator.applyAsInt(anyInt())).thenReturn(sizeEstimate);
        source.sendItems(item);
        verify(allocator).newBuffer(sizeEstimate);
        assertThat("Unexpected created buffers.", createdBuffers, hasSize(1));
        final Buffer serialized = createdBuffers.remove(0);
        verify(sizeEstimator).applyAsInt(sizeEstimate - 1);
        verify(serializer).serialize(item, serialized);
        return serialized;
    }
}
