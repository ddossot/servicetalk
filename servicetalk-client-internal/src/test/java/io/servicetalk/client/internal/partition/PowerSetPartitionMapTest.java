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
package io.servicetalk.client.internal.partition;

import io.servicetalk.client.api.partition.PartitionAttributes;
import io.servicetalk.client.api.partition.PartitionAttributes.Key;
import io.servicetalk.client.api.partition.PartitionAttributesBuilder;
import io.servicetalk.concurrent.api.Completable;
import io.servicetalk.concurrent.api.CompletableProcessor;
import io.servicetalk.concurrent.api.ListenableAsyncCloseable;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PowerSetPartitionMapTest {
    private static final Key<Integer> DC_ID = Key.newKeyWithDebugToString("dc");
    private static final Key<String> APP_ID = Key.newKeyWithDebugToString("app");
    private static final Key<Integer> SHARD_ID = Key.newKeyWithDebugToString("shard");
    private static final Key<Boolean> IS_MASTER = Key.newKeyWithDebugToString("master");
    private static final Key<Boolean> EXTRA = Key.newKeyWithDebugToString("extra");
    private static final ListenableAsyncCloseable VALUE = new ListenableAsyncCloseable() {
        private final CompletableProcessor close = new CompletableProcessor();

        @Override
        public Completable onClose() {
            return close;
        }

        @Override
        public Completable closeAsync() {
            return new Completable() {
                @Override
                protected void handleSubscribe(Subscriber subscriber) {
                    close.onComplete();
                    close.subscribe(subscriber);
                }
            };
        }
    };

    @Test
    public void testDuplicatePutMakesNoChange() {
        PowerSetPartitionMap<ListenableAsyncCloseable> map = oneTwoThreeMap();
        // Test duplicate put
        List<ListenableAsyncCloseable> result = map.addPartition(host3Attributes());
        assertOneTwoThree(map);
        assertEquals(15, result.size());
        assertEquals(VALUE, map.getPartition(host3Attributes()));
    }

    @Test
    public void testRemoveOfOverlappingAttributesPreservesValue() {
        PowerSetPartitionMap<ListenableAsyncCloseable> map = oneTwoThreeMap();
        // Test remove
        List<ListenableAsyncCloseable> result = map.removePartition(host3Attributes());
        assertEquals(VALUE, map.getPartition(host1Attributes()));
        assertEquals(VALUE, map.getPartition(host2Attributes()));
        assertNull(map.getPartition(host3Attributes()));
        assertEquals(15, result.size());

        // Test a wild card with a unique attribute to host3 removed no longer returns any results.
        PartitionAttributesBuilder builder = new DefaultPartitionAttributesBuilder(1);
        builder.add(SHARD_ID, 9);
        PartitionAttributes partitionAttributes = builder.build();
        assertNull(map.getPartition(partitionAttributes));

        // Test the same key, but different value still returns results.
        builder = new DefaultPartitionAttributesBuilder(1);
        builder.add(SHARD_ID, 10);
        partitionAttributes = builder.build();
        assertNotNull(map.getPartition(partitionAttributes));
    }

    @Test
    public void testWildCardResolveSingleElement() {
        PowerSetPartitionMap<ListenableAsyncCloseable> map = oneTwoThreeMap();

        PartitionAttributesBuilder builder = new DefaultPartitionAttributesBuilder(1);
        builder.add(DC_ID, 1);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(1);
        builder.add(SHARD_ID, 10);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(1);
        builder.add(SHARD_ID, 9);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(1);
        builder.add(APP_ID, "myapp");
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(1);
        builder.add(IS_MASTER, false);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(1);
        builder.add(IS_MASTER, true);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(1);
        builder.add(APP_ID, "notmyapp");
        assertNull(map.getPartition(builder.build()));
    }

    @Test
    public void testWildCardResolveTwoElements() {
        PowerSetPartitionMap<ListenableAsyncCloseable> map = oneTwoThreeMap();

        PartitionAttributesBuilder builder = new DefaultPartitionAttributesBuilder(2);
        builder.add(DC_ID, 1);
        builder.add(SHARD_ID, 10);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(2);
        builder.add(DC_ID, 1);
        builder.add(SHARD_ID, 9);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(2);
        builder.add(DC_ID, 1);
        builder.add(APP_ID, "myapp");
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(2);
        builder.add(APP_ID, "myapp");
        builder.add(IS_MASTER, true);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(2);
        builder.add(APP_ID, "myapp");
        builder.add(IS_MASTER, false);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(2);
        builder.add(DC_ID, 1);
        builder.add(IS_MASTER, false);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(2);
        builder.add(DC_ID, 1);
        builder.add(IS_MASTER, true);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(2);
        builder.add(DC_ID, 2);
        builder.add(IS_MASTER, true);
        assertNull(map.getPartition(builder.build()));
    }

    @Test
    public void testWildCardResolveThreeElements() {
        PowerSetPartitionMap<ListenableAsyncCloseable> map = oneTwoThreeMap();

        PartitionAttributesBuilder builder = new DefaultPartitionAttributesBuilder(3);
        builder.add(DC_ID, 1);
        builder.add(SHARD_ID, 10);
        builder.add(APP_ID, "myapp");
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(3);
        builder.add(DC_ID, 1);
        builder.add(SHARD_ID, 9);
        builder.add(APP_ID, "myapp");
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(3);
        builder.add(DC_ID, 1);
        builder.add(APP_ID, "myapp");
        builder.add(IS_MASTER, true);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(3);
        builder.add(DC_ID, 1);
        builder.add(APP_ID, "myapp");
        builder.add(IS_MASTER, false);
        assertEquals(VALUE, map.getPartition(builder.build()));

        builder = new DefaultPartitionAttributesBuilder(3);
        builder.add(DC_ID, 1);
        builder.add(APP_ID, "notmyapp");
        builder.add(IS_MASTER, true);
        assertNull(map.getPartition(builder.build()));
    }

    @Test
    public void testResolveFourElements() {
        PowerSetPartitionMap<ListenableAsyncCloseable> map = oneTwoThreeMap();

        assertEquals(VALUE, map.getPartition(host1Attributes()));
        assertEquals(VALUE, map.getPartition(host2Attributes()));
        assertEquals(VALUE, map.getPartition(host3Attributes()));
        assertNull(map.getPartition(host4Attributes()));

        // Add an extra attribute to host3 and test that it doesn't resolve.
        PartitionAttributesBuilder builder = new DefaultPartitionAttributesBuilder(5);
        builder.add(DC_ID, 1);
        builder.add(SHARD_ID, 9);
        builder.add(APP_ID, "myapp");
        builder.add(IS_MASTER, true);
        builder.add(EXTRA, true);
        assertNull(map.getPartition(builder.build()));
    }

    @Test
    public void testAddEmptyPartitionAttributesThrows() {
        PowerSetPartitionMap<ListenableAsyncCloseable> map = new PowerSetPartitionMap<>(address -> VALUE);
        PartitionAttributes emptyAttributes = new DefaultPartitionAttributesBuilder(0).build();
        try {
            map.addPartition(emptyAttributes);
            fail();
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void testAddDuplicationPartitions() {
        PowerSetPartitionMap<ListenableAsyncCloseable> map = new PowerSetPartitionMap<>(address -> VALUE);
        assertTrue("New map is not empty.", map.isEmpty());
        PartitionAttributes partition = new DefaultPartitionAttributesBuilder(1).add(IS_MASTER, true).add(SHARD_ID, 1).build();
        List<ListenableAsyncCloseable> added1 = map.addPartition(partition);
        List<ListenableAsyncCloseable> added2 = map.addPartition(partition);
        assertEquals("Added partitions are not equal.", added1, added2);
        assertEquals("Same partition added twice.", map.size(), 1);

        List<ListenableAsyncCloseable> removed = map.removePartition(partition);
        assertEquals("Unexpected size of removed partitions.", removed.size(), added1.size());
    }

    private static void assertMapSize(PowerSetPartitionMap<ListenableAsyncCloseable> map, int size, int indexSize) {
        assertEquals(size == 0, map.isEmpty());
        assertEquals(size, map.size());
        assertEquals(indexSize, map.wildCardIndexSize());
    }

    private static void assertOneTwoThree(PowerSetPartitionMap<ListenableAsyncCloseable> map) {
        assertMapSize(map, 3, 31);
    }

    private static PowerSetPartitionMap<ListenableAsyncCloseable> oneTwoThreeMap() {
        PowerSetPartitionMap<ListenableAsyncCloseable> map = new PowerSetPartitionMap<>(address -> VALUE);
        List<ListenableAsyncCloseable> result = map.addPartition(host1Attributes());
        assertEquals(VALUE, map.getPartition(host1Attributes()));
        assertEquals(15, result.size());
        assertMapSize(map, 1, 15);
        result = map.addPartition(host2Attributes());
        assertEquals(VALUE, map.getPartition(host2Attributes()));
        assertEquals(15, result.size());
        assertMapSize(map, 2, 23);
        result = map.addPartition(host3Attributes());
        assertEquals(VALUE, map.getPartition(host3Attributes()));
        assertEquals(15, result.size());
        assertOneTwoThree(map);
        return map;
    }

    private static PartitionAttributes host1Attributes() {
        PartitionAttributesBuilder builder = new DefaultPartitionAttributesBuilder(4);
        builder.add(DC_ID, 1);
        builder.add(SHARD_ID, 10);
        builder.add(APP_ID, "myapp");
        builder.add(IS_MASTER, false);
        return builder.build();
    }

    private static PartitionAttributes host2Attributes() {
        PartitionAttributesBuilder builder = new DefaultPartitionAttributesBuilder(4);
        builder.add(DC_ID, 1);
        builder.add(SHARD_ID, 10);
        builder.add(APP_ID, "myapp");
        builder.add(IS_MASTER, true);
        return builder.build();
    }

    private static PartitionAttributes host3Attributes() {
        PartitionAttributesBuilder builder = new DefaultPartitionAttributesBuilder(4);
        builder.add(DC_ID, 1);
        builder.add(SHARD_ID, 9);
        builder.add(APP_ID, "myapp");
        builder.add(IS_MASTER, true);
        return builder.build();
    }

    private static PartitionAttributes host4Attributes() {
        PartitionAttributesBuilder builder = new DefaultPartitionAttributesBuilder(4);
        builder.add(DC_ID, 1);
        builder.add(SHARD_ID, 8);
        builder.add(APP_ID, "myapp");
        builder.add(IS_MASTER, true);
        return builder.build();
    }
}
