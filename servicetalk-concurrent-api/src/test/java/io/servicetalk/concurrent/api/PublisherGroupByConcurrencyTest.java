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
package io.servicetalk.concurrent.api;

import io.servicetalk.concurrent.internal.ServiceTalkTestTimeout;
import io.servicetalk.concurrent.internal.TerminalNotification;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public final class PublisherGroupByConcurrencyTest {
    @Rule
    public final Timeout timeout = new ServiceTalkTestTimeout(30, SECONDS);
    @Rule
    public final MockedSubscriberRule<Integer> groupsSubscriber = new MockedSubscriberRule<>();

    private ConcurrentLinkedQueue<Integer> allItemsReceivedOnAllGroups;
    private TestPublisher<Integer> source;
    private ExecutorService executor;
    private AtomicBoolean allWorkDone;

    @Before
    public void setUp() throws Exception {
        source = new TestPublisher<>();
        source.sendOnSubscribe();
        allItemsReceivedOnAllGroups = new ConcurrentLinkedQueue<>();
        executor = newCachedThreadPool();
        allWorkDone = new AtomicBoolean();
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdown();
        allItemsReceivedOnAllGroups.clear();
    }

    @Test
    public void testConcurrentEmissionAndGroupCancel() throws Exception {
        int itemCount = 100_000;
        Queue<GroupSubscriber> subs = subscribeToAll(itemCount, true);
        Task cancels = drainGroupSubscribers(subs, GroupSubscriber::cancel).awaitStart();
        sendRangeToSource(0, itemCount).onComplete();
        cancels.awaitCompletion();
        assertThat("Unexpected items received.", allItemsReceivedOnAllGroups, hasSize(itemCount));
    }

    @Test
    public void testConcurrentEmissionAndGroupRequestN() throws Exception {
        int itemCount = 100_000;
        Queue<GroupSubscriber> subs = subscribeToAll(itemCount, false);
        Task requestNs = requestAndDrainGroupSubscribers(subs).awaitStart();
        sendRangeToSource(0, itemCount).onComplete();
        requestNs.awaitCompletion();
        assertThat("Unexpected items received.", allItemsReceivedOnAllGroups, hasSize(itemCount));
    }

    @Test
    public void testConcurrentGroupsCancel() throws Exception {
        int itemCount = 100_000;
        Queue<GroupSubscriber> subs = subscribeToAll(itemCount, false);
        Task requestNs = requestAndDrainGroupSubscribers(subs).awaitStart();
        sendRangeToSource(0, itemCount);
        groupsSubscriber.cancel();
        requestNs.awaitCompletion();
        assertThat("Unexpected items received.", allItemsReceivedOnAllGroups, hasSize(itemCount));
        source.verifyCancelled();
    }

    private Queue<GroupSubscriber> subscribeToAll(int bufferSize, boolean requestFromEachGroupOnSubscribe) {
        ConcurrentLinkedQueue<GroupSubscriber> subs = new ConcurrentLinkedQueue<>();
        groupsSubscriber.subscribe(source.groupBy(integer -> integer, bufferSize).map(grp -> {
            GroupSubscriber sub = new GroupSubscriber();
            // Each group must only ever get one item.
            grp.doBeforeNext(integer -> allItemsReceivedOnAllGroups.add(integer)).subscribe(sub);
            if (requestFromEachGroupOnSubscribe) {
                sub.request(1); // Only one item ever comes on every group as each int is a new group.
            }
            subs.add(sub);
            return grp.getKey();
        })).request(Long.MAX_VALUE);
        return subs;
    }

    private TestPublisher<Integer> sendRangeToSource(int start, int end) {
        for (int i = start; i < end; i++) {
            source.sendItemsNoDemandCheck(i);
        }
        return source;
    }

    private Task drainGroupSubscribers(Queue<GroupSubscriber> subscribers, Predicate<GroupSubscriber> shouldRemoveSubscriber) {
        return runThis(() -> {

            for (;;) {
                GroupSubscriber poll = subscribers.poll();
                if (poll == null) {
                    return;
                }
                if (!shouldRemoveSubscriber.test(poll)) {
                    subscribers.add(poll);
                }
            }
        });
    }

    private Task drainGroupSubscribers(Queue<GroupSubscriber> subscribers, Consumer<GroupSubscriber> readySubConsumer) {
        return drainGroupSubscribers(subscribers, sub -> {
            if (sub.isOnNextReceived(1)) {
                readySubConsumer.accept(sub);
                return true;
            }
            return false;
        });
    }

    private Task requestAndDrainGroupSubscribers(Queue<GroupSubscriber> subscribers) {
        return drainGroupSubscribers(subscribers, sub -> {
            if (!sub.isOnNextReceived(1)) {
                sub.request(1);
                return false;
            }
            return true;
        });
    }

    private Task runThis(Runnable task) {
        return new Task(task);
    }

    private final class Task {

        private final CountDownLatch started = new CountDownLatch(1);
        private final Future<?> result;

        Task(Runnable delegate) {
            result = executor.submit(() -> {
                started.countDown();
                do {
                    delegate.run();
                } while (!allWorkDone.get());
                delegate.run(); // Run once more to make sure we consume all work.
            });
        }

        Task awaitStart() throws InterruptedException {
            started.await();
            return this;
        }

        void awaitCompletion() throws InterruptedException, ExecutionException {
            allWorkDone.set(true);
            result.get();
        }
    }

    private static final class GroupSubscriber implements Subscriber<Integer>, Subscription {

        private final Queue<Integer> nexts = new ConcurrentLinkedQueue<>();
        @Nullable
        private volatile Subscription subscription;
        @Nullable
        private volatile TerminalNotification terminalNotification;

        @Override
        public void onSubscribe(Subscription s) {
            subscription = s;
        }

        @Override
        public void onNext(Integer integer) {
            nexts.add(integer);
        }

        @Override
        public void onError(Throwable t) {
            terminalNotification = TerminalNotification.error(t);
        }

        @Override
        public void onComplete() {
            terminalNotification = TerminalNotification.complete();
        }

        @Override
        public void request(long n) {
            Subscription s = subscription;
            assert s != null : "Subscription is null.";

            s.request(n);
        }

        @Override
        public void cancel() {
            Subscription s = subscription;
            assert s != null : "Subscription is null.";

            s.cancel();
        }

        boolean isOnNextReceived(int onNextCount) {
            return nexts.size() == onNextCount;
        }

        void verifyCompleted() {
            assertThat("Unexpected terminal state.", terminalNotification, is(TerminalNotification.complete()));
        }

        void verifyError(Class<? extends Throwable> errorType) {
            TerminalNotification t = this.terminalNotification;
            assert t != null : "Group subscriber not terminated.";
            assertThat("Unexpected terminal state.", t.getCause(), is(instanceOf(errorType)));
        }
    }
}
