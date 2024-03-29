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
package io.servicetalk.concurrent.context;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import javax.annotation.Nullable;

import static java.lang.System.arraycopy;

/**
 * This class provides a Copy-on-Write map behavior is and special cased for cardinality of less than 7 elements. Less
 * than 7 elements was chosen because it is not common to have more than this number of {@link Key}-value pairs in a
 * single {@link AsyncContextMap}. Common {@link Key}-value paris are (tracing, MDC, auth, 3-custom user entries).
 */
final class CopyOnWriteAsyncContextMap implements AsyncContextMap {
    static final AsyncContextMap EMPTY_CONTEXT_MAP = new CopyOnWriteAsyncContextMap();

    private CopyOnWriteAsyncContextMap() {
        // singleton
    }

    @Nullable
    @Override
    public <T> T get(final Key<T> key) {
        return null;
    }

    @Override
    public boolean contains(final Key<?> key) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public <T> AsyncContextMap put(final Key<T> key, @Nullable final T value) {
        return new OneAsyncContextMap(key, value);
    }

    @Override
    public AsyncContextMap putAll(final AsyncContextMap context) {
        return context;
    }

    @Override
    public AsyncContextMap putAll(final Map<Key<?>, Object> map) {
        switch (map.size()) {
            case 0:
                return this;
            case 1: {
                OneAsyncContextMap newMap = new OneAsyncContextMap();
                map.forEach(newMap);
                return newMap;
            }
            case 2: {
                TwoAsyncContextMap newMap = new TwoAsyncContextMap();
                map.forEach(newMap);
                return newMap;
            }
            case 3: {
                ThreeAsyncContextMap newMap = new ThreeAsyncContextMap();
                map.forEach(newMap);
                return newMap;
            }
            case 4: {
                FourAsyncContextMap newMap = new FourAsyncContextMap();
                map.forEach(newMap);
                return newMap;
            }
            case 5: {
                FiveAsyncContextMap newMap = new FiveAsyncContextMap();
                map.forEach(newMap);
                return newMap;
            }
            case 6:
                SixAsyncContextMap newMap = new SixAsyncContextMap();
                map.forEach(newMap);
                return newMap;
            default:
                return new SevenOrMoreAsyncContextMap().putAll(map);
        }
    }

    @Override
    public AsyncContextMap remove(final Key<?> key) {
        return this;
    }

    @Override
    public AsyncContextMap removeAll(final AsyncContextMap context) {
        return this;
    }

    @Override
    public AsyncContextMap removeAll(final Iterable<Key<?>> entries) {
        return this;
    }

    @Override
    public AsyncContextMap clear() {
        return this;
    }

    @Nullable
    @Override
    public Key<?> forEach(final BiPredicate<Key<?>, Object> consumer) {
        return null;
    }

    private static final class OneAsyncContextMap implements AsyncContextMap, BiConsumer<Key<?>, Object>,
                                                             BiPredicate<Key<?>, Object> {
        @Nullable
        private Key<?> key;
        @Nullable
        private Object value;

        OneAsyncContextMap() {
        }

        OneAsyncContextMap(Key<?> key, @Nullable Object value) {
            this.key = key;
            this.value = value;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T get(final Key<T> key) {
            assert this.key != null;
            return this.key.equals(key) ? (T) value : null;
        }

        @Override
        public boolean contains(final Key<?> key) {
            assert this.key != null;
            return this.key.equals(key);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public <T> AsyncContextMap put(final Key<T> key, @Nullable final T value) {
            assert this.key != null;
            if (this.key.equals(key)) {
                return new OneAsyncContextMap(this.key, value);
            }
            return new TwoAsyncContextMap(this.key, this.value, key, value);
        }

        @Override
        public AsyncContextMap putAll(final AsyncContextMap context) {
            assert key != null;
            switch (context.size()) {
                case 0:
                    return this;
                case 1: {
                    PutTwoBuilder builder = new PutTwoBuilder(key, value);
                    context.forEach(builder);
                    return builder.build();
                }
                case 2: {
                    PutThreeBuilder builder = new PutThreeBuilder(key, value);
                    context.forEach(builder);
                    return builder.build();
                }
                case 3: {
                    PutFourBuilder builder = new PutFourBuilder(key, value);
                    context.forEach(builder);
                    return builder.build();
                }
                case 4: {
                    PutFiveBuilder builder = new PutFiveBuilder(key, value);
                    context.forEach(builder);
                    return builder.build();
                }
                case 5:
                    PutSixBuilder builder = new PutSixBuilder(key, value);
                    context.forEach(builder);
                    return builder.build();
                default:
                    return new SevenOrMoreAsyncContextMap(key, value).putAll(context);
            }
        }

        @Override
        public AsyncContextMap putAll(final Map<Key<?>, Object> map) {
            assert key != null;
            switch (map.size()) {
                case 0:
                    return this;
                case 1: {
                    PutTwoBuilder builder = new PutTwoBuilder(key, value);
                    map.forEach(builder);
                    return builder.build();
                }
                case 2: {
                    PutThreeBuilder builder = new PutThreeBuilder(key, value);
                    map.forEach(builder);
                    return builder.build();
                }
                case 3: {
                    PutFourBuilder builder = new PutFourBuilder(key, value);
                    map.forEach(builder);
                    return builder.build();
                }
                case 4: {
                    PutFiveBuilder builder = new PutFiveBuilder(key, value);
                    map.forEach(builder);
                    return builder.build();
                }
                case 5:
                    PutSixBuilder builder = new PutSixBuilder(key, value);
                    map.forEach(builder);
                    return builder.build();
                default:
                    return new SevenOrMoreAsyncContextMap(key, value).putAll(map);
            }
        }

        @Override
        public AsyncContextMap remove(final Key<?> key) {
            assert this.key != null;
            return this.key.equals(key) ? EMPTY_CONTEXT_MAP : this;
        }

        @Override
        public AsyncContextMap removeAll(final AsyncContextMap context) {
            return context.forEach(this) == key ? EMPTY_CONTEXT_MAP : this;
        }

        @Override
        public AsyncContextMap removeAll(final Iterable<Key<?>> entries) {
            assert this.key != null;
            MutableInt index = new MutableInt();
            entries.forEach(key -> {
                if (this.key.equals(key)) {
                    index.value = 1;
                }
            });
            return index.value != 0 ? EMPTY_CONTEXT_MAP : this;
        }

        @Override
        public AsyncContextMap clear() {
            return EMPTY_CONTEXT_MAP;
        }

        @Nullable
        @Override
        public Key<?> forEach(final BiPredicate<Key<?>, Object> consumer) {
            return consumer.test(key, value) ? null : key;
        }

        @Override
        public void accept(final Key<?> key, final Object value) {
            test(key, value);
        }

        @Override
        public boolean test(final Key<?> key, final Object o) {
            assert this.key == null;
            this.key = key;
            this.value = o;
            return false;
        }

        private static final class PutTwoBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            @Nullable
            private Key<?> keyTwo;
            @Nullable
            private Object valueTwo;

            PutTwoBuilder(Key<?> keyOne, @Nullable Object valueOne) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else {
                    assert keyTwo == null;
                    keyTwo = key;
                    valueTwo = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                if (keyTwo == null) {
                    return new OneAsyncContextMap(keyOne, valueOne);
                }
                return new TwoAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo);
            }
        }

        private static final class PutThreeBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            @Nullable
            private Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            @Nullable
            private Key<?> keyThree;
            @Nullable
            private Object valueThree;

            PutThreeBuilder(Key<?> keyOne, @Nullable Object valueOne) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo == null) {
                    keyTwo = key;
                    valueTwo = o;
                } else {
                    assert keyThree == null;
                    keyThree = key;
                    valueThree = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                assert keyTwo != null;
                if (keyThree == null) {
                    return new TwoAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo);
                }
                return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
            }
        }

        private static final class PutFourBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            @Nullable
            private Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            @Nullable
            private Key<?> keyThree;
            @Nullable
            private Object valueThree;
            @Nullable
            private Key<?> keyFour;
            @Nullable
            private Object valueFour;

            PutFourBuilder(Key<?> keyOne, @Nullable Object valueOne) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo == null) {
                    keyTwo = key;
                    valueTwo = o;
                } else if (keyThree == null) {
                    keyThree = key;
                    valueThree = o;
                } else {
                    assert keyFour == null;
                    keyFour = key;
                    valueFour = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                assert keyTwo != null && keyThree != null;
                if (keyFour == null) {
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
                }
                return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour);
            }
        }

        private static final class PutFiveBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            @Nullable
            private Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            @Nullable
            private Key<?> keyThree;
            @Nullable
            private Object valueThree;
            @Nullable
            private Key<?> keyFour;
            @Nullable
            private Object valueFour;
            @Nullable
            private Key<?> keyFive;
            @Nullable
            private Object valueFive;

            PutFiveBuilder(Key<?> keyOne, @Nullable Object valueOne) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo == null) {
                    keyTwo = key;
                    valueTwo = o;
                } else if (keyThree == null) {
                    keyThree = key;
                    valueThree = o;
                } else if (keyFour == null) {
                    keyFour = key;
                    valueFour = o;
                } else {
                    assert keyFive == null;
                    keyFive = key;
                    valueFive = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                assert keyTwo != null && keyThree != null && keyFour != null;
                if (keyFive == null) {
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour);
                }
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive);
            }
        }

        private static final class PutSixBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            @Nullable
            private Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            @Nullable
            private Key<?> keyThree;
            @Nullable
            private Object valueThree;
            @Nullable
            private Key<?> keyFour;
            @Nullable
            private Object valueFour;
            @Nullable
            private Key<?> keyFive;
            @Nullable
            private Object valueFive;
            @Nullable
            private Key<?> keySix;
            @Nullable
            private Object valueSix;

            PutSixBuilder(Key<?> keyOne, @Nullable Object valueOne) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo == null) {
                    keyTwo = key;
                    valueTwo = o;
                } else if (keyThree == null) {
                    keyThree = key;
                    valueThree = o;
                } else if (keyFour == null) {
                    keyFour = key;
                    valueFour = o;
                } else if (keyFive == null) {
                    keyFive = key;
                    valueFive = o;
                } else {
                    assert keySix == null;
                    keySix = key;
                    valueSix = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                assert keyTwo != null && keyThree != null && keyFour != null && keyFive != null;
                if (keySix == null) {
                    return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour, keyFive, valueFive);
                }
                return new SixAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive, keySix, valueSix);
            }
        }
    }

    private static final class TwoAsyncContextMap implements AsyncContextMap, BiConsumer<Key<?>, Object>,
                                                             BiPredicate<Key<?>, Object> {
        @Nullable
        private Key<?> keyOne;
        @Nullable
        private Object valueOne;
        @Nullable
        private Key<?> keyTwo;
        @Nullable
        private Object valueTwo;

        TwoAsyncContextMap() {
        }

        TwoAsyncContextMap(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo) {
            this.keyOne = keyOne;
            this.valueOne = valueOne;
            this.keyTwo = keyTwo;
            this.valueTwo = valueTwo;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T get(final Key<T> key) {
            assert keyOne != null && keyTwo != null;
            return keyOne.equals(key) ? (T) valueOne : keyTwo.equals(key) ? (T) valueTwo : null;
        }

        @Override
        public boolean contains(final Key<?> key) {
            assert keyOne != null && keyTwo != null;
            return keyOne.equals(key) || keyTwo.equals(key);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 2;
        }

        @Override
        public <T> AsyncContextMap put(final Key<T> key, @Nullable final T value) {
            assert keyOne != null && keyTwo != null;
            if (keyOne.equals(key)) {
                return new TwoAsyncContextMap(keyOne, value, keyTwo, valueTwo);
            } else if (keyTwo.equals(key)) {
                return new TwoAsyncContextMap(keyOne, valueOne, keyTwo, value);
            }
            return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, key, value);
        }

        @Override
        public AsyncContextMap putAll(final AsyncContextMap context) {
            assert keyOne != null && keyTwo != null;
            switch (context.size()) {
                case 0:
                    return this;
                case 1: {
                    PutThreeBuilder builder = new PutThreeBuilder(keyOne, valueOne, keyTwo, valueTwo);
                    context.forEach(builder);
                    return builder.build();
                }
                case 2: {
                    PutFourBuilder builder = new PutFourBuilder(keyOne, valueOne, keyTwo, valueTwo);
                    context.forEach(builder);
                    return builder.build();
                }
                case 3: {
                    PutFiveBuilder builder = new PutFiveBuilder(keyOne, valueOne, keyTwo, valueTwo);
                    context.forEach(builder);
                    return builder.build();
                }
                case 4: {
                    PutSixBuilder builder = new PutSixBuilder(keyOne, valueOne, keyTwo, valueTwo);
                    context.forEach(builder);
                    return builder.build();
                }
                default:
                    PutSevenBuilder builder = new PutSevenBuilder(keyOne, valueOne, keyTwo, valueTwo, context.size());
                    context.forEach(builder);
                    return builder.build();
            }
        }

        @Override
        public AsyncContextMap putAll(final Map<Key<?>, Object> map) {
            assert keyOne != null && keyTwo != null;
            switch (map.size()) {
                case 0:
                    return this;
                case 1: {
                    PutThreeBuilder builder = new PutThreeBuilder(keyOne, valueOne, keyTwo, valueTwo);
                    map.forEach(builder);
                    return builder.build();
                }
                case 2: {
                    PutFourBuilder builder = new PutFourBuilder(keyOne, valueOne, keyTwo, valueTwo);
                    map.forEach(builder);
                    return builder.build();
                }
                case 3: {
                    PutFiveBuilder builder = new PutFiveBuilder(keyOne, valueOne, keyTwo, valueTwo);
                    map.forEach(builder);
                    return builder.build();
                }
                case 4: {
                    PutSixBuilder builder = new PutSixBuilder(keyOne, valueOne, keyTwo, valueTwo);
                    map.forEach(builder);
                    return builder.build();
                }
                default:
                    PutSevenBuilder builder = new PutSevenBuilder(keyOne, valueOne, keyTwo, valueTwo, map.size());
                    map.forEach(builder);
                    return builder.build();
            }
        }

        @Override
        public AsyncContextMap remove(final Key<?> key) {
            assert keyOne != null && keyTwo != null;
            return keyOne.equals(key) ? new OneAsyncContextMap(keyTwo, valueTwo) : keyTwo.equals(key) ?
                    new OneAsyncContextMap(keyOne, valueOne) : this;
        }

        @Override
        public AsyncContextMap removeAll(final AsyncContextMap context) {
            assert keyOne != null && keyTwo != null;
            MutableInt removeIndexMask = new MutableInt();
            context.forEach((k, v) -> {
                if (keyOne.equals(k)) {
                    removeIndexMask.value |= 0x1;
                    return removeIndexMask.value != 0x3;
                } else if (keyTwo.equals(k)) {
                    removeIndexMask.value |= 0x2;
                    return removeIndexMask.value != 0x3;
                }
                return true;
            });
            return removeAll(removeIndexMask);
        }

        @Override
        public AsyncContextMap removeAll(final Iterable<Key<?>> entries) {
            assert keyOne != null && keyTwo != null;
            MutableInt removeIndexMask = new MutableInt();
            entries.forEach(k -> {
                if (keyOne.equals(k)) {
                    removeIndexMask.value |= 0x1;
                } else if (keyTwo.equals(k)) {
                    removeIndexMask.value |= 0x2;
                }
            });
            return removeAll(removeIndexMask);
        }

        private AsyncContextMap removeAll(MutableInt removeIndexMask) {
            assert keyOne != null && keyTwo != null;
            if ((removeIndexMask.value & 0x3) == 0x3) {
                return EMPTY_CONTEXT_MAP;
            } else if ((removeIndexMask.value & 0x2) == 0x2) {
                return new OneAsyncContextMap(keyOne, valueOne);
            } else if ((removeIndexMask.value & 0x1) == 0x1) {
                return new OneAsyncContextMap(keyTwo, valueTwo);
            }
            return this;
        }

        @Override
        public AsyncContextMap clear() {
            return EMPTY_CONTEXT_MAP;
        }

        @Nullable
        @Override
        public Key<?> forEach(final BiPredicate<Key<?>, Object> consumer) {
            if (!consumer.test(keyOne, valueOne)) {
                return keyOne;
            }
            return consumer.test(keyTwo, valueTwo) ? null : keyTwo;
        }

        @Override
        public void accept(final Key<?> key, final Object value) {
            test(key, value);
        }

        @Override
        public boolean test(final Key<?> key, final Object value) {
            if (keyOne == null) {
                keyOne = key;
                valueOne = value;
            } else {
                assert keyTwo == null;
                keyTwo = key;
                valueTwo = value;
                return false;
            }
            return true;
        }

        private static final class PutThreeBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            private final Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            @Nullable
            private Key<?> keyThree;
            @Nullable
            private Object valueThree;

            PutThreeBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
                this.keyTwo = keyTwo;
                this.valueTwo = valueTwo;
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo.equals(key)) {
                    valueTwo = o;
                } else {
                    assert keyThree == null;
                    keyThree = key;
                    valueThree = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                return (keyThree == null) ? new TwoAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo) :
                        new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }
        }

        private static final class PutFourBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            private final Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            @Nullable
            private Key<?> keyThree;
            @Nullable
            private Object valueThree;
            @Nullable
            private Key<?> keyFour;
            @Nullable
            private Object valueFour;

            PutFourBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
                this.keyTwo = keyTwo;
                this.valueTwo = valueTwo;
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo.equals(key)) {
                    valueTwo = o;
                } else if (keyThree == null) {
                    keyThree = key;
                    valueThree = o;
                } else {
                    assert keyFour == null;
                    keyFour = key;
                    valueFour = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                if (keyThree == null) {
                    return new TwoAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo);
                }
                if (keyFour == null) {
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
                }
                return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour);
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }
        }

        private static final class PutFiveBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            private final Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            @Nullable
            private Key<?> keyThree;
            @Nullable
            private Object valueThree;
            @Nullable
            private Key<?> keyFour;
            @Nullable
            private Object valueFour;
            @Nullable
            private Key<?> keyFive;
            @Nullable
            private Object valueFive;

            PutFiveBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
                this.keyTwo = keyTwo;
                this.valueTwo = valueTwo;
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo.equals(key)) {
                    valueTwo = o;
                } else if (keyThree == null) {
                    keyThree = key;
                    valueThree = o;
                } else if (keyFour == null) {
                    keyFour = key;
                    valueFour = o;
                } else {
                    assert keyFive == null;
                    keyFive = key;
                    valueFive = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                if (keyThree == null) {
                    return new TwoAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo);
                }
                if (keyFour == null) {
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
                }
                if (keyFive == null) {
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour);
                }
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive);
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }
        }

        private static final class PutSixBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            private final Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            @Nullable
            private Key<?> keyThree;
            @Nullable
            private Object valueThree;
            @Nullable
            private Key<?> keyFour;
            @Nullable
            private Object valueFour;
            @Nullable
            private Key<?> keyFive;
            @Nullable
            private Object valueFive;
            @Nullable
            private Key<?> keySix;
            @Nullable
            private Object valueSix;

            PutSixBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
                this.keyTwo = keyTwo;
                this.valueTwo = valueTwo;
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo.equals(key)) {
                    valueTwo = o;
                } else if (keyThree == null) {
                    keyThree = key;
                    valueThree = o;
                } else if (keyFour == null) {
                    keyFour = key;
                    valueFour = o;
                } else if (keyFive == null) {
                    keyFive = key;
                    valueFive = o;
                } else {
                    assert keySix == null;
                    keySix = key;
                    valueSix = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                if (keyThree == null) {
                    return new TwoAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo);
                }
                if (keyFour == null) {
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
                }
                if (keyFive == null) {
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour);
                }
                if (keySix == null) {
                    return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour, keyFive, valueFive);
                }
                return new SixAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive, keySix, valueSix);
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }
        }

        private static final class PutSevenBuilder extends AbstractPutSevenBuilder {
            PutSevenBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                            int putAllMapSize) {
                super((2 + putAllMapSize) << 1, 4);
                pairs[0] = keyOne;
                pairs[1] = valueOne;
                pairs[2] = keyTwo;
                pairs[3] = valueTwo;
            }

            AsyncContextMap build() {
                if (nextIndex == 4) {
                    return new TwoAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3]);
                }
                if (nextIndex == 6) {
                    return new ThreeAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5]);
                }
                if (nextIndex == 8) {
                    return new FourAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7]);
                }
                if (nextIndex == 10) {
                    return new FiveAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7], (Key<?>) pairs[8], pairs[9]);
                }
                if (nextIndex == 12) {
                    return new SixAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7], (Key<?>) pairs[8], pairs[9],
                            (Key<?>) pairs[10], pairs[11]);
                }
                if (nextIndex == pairs.length) {
                    return new SevenOrMoreAsyncContextMap(pairs);
                }
                return new SevenOrMoreAsyncContextMap(Arrays.copyOf(pairs, nextIndex));
            }
        }
    }

    private static final class ThreeAsyncContextMap implements AsyncContextMap, BiConsumer<Key<?>, Object>,
                                                               BiPredicate<Key<?>, Object> {
        @Nullable
        private Key<?> keyOne;
        @Nullable
        private Object valueOne;
        @Nullable
        private Key<?> keyTwo;
        @Nullable
        private Object valueTwo;
        @Nullable
        private Key<?> keyThree;
        @Nullable
        private Object valueThree;

        ThreeAsyncContextMap() {
        }

        ThreeAsyncContextMap(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                             Key<?> keyThree, @Nullable Object valueThree) {
            this.keyOne = keyOne;
            this.valueOne = valueOne;
            this.keyTwo = keyTwo;
            this.valueTwo = valueTwo;
            this.keyThree = keyThree;
            this.valueThree = valueThree;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T get(final Key<T> key) {
            assert keyOne != null && keyTwo != null && keyThree != null;
            return keyOne.equals(key) ? (T) valueOne : keyTwo.equals(key) ? (T) valueTwo : keyThree.equals(key) ?
                    (T) valueThree : null;
        }

        @Override
        public boolean contains(final Key<?> key) {
            assert keyOne != null && keyTwo != null && keyThree != null;
            return keyOne.equals(key) || keyTwo.equals(key) || keyThree.equals(key);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 3;
        }

        @Override
        public <T> AsyncContextMap put(final Key<T> key, @Nullable final T value) {
            assert keyOne != null && keyTwo != null && keyThree != null;
            if (keyOne.equals(key)) {
                return new ThreeAsyncContextMap(keyOne, value, keyTwo, valueTwo, keyThree, valueThree);
            } else if (keyTwo.equals(key)) {
                return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, value, keyThree, valueThree);
            } else if (keyThree.equals(key)) {
                return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, value);
            }
            return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree, key, value);
        }

        @Override
        public AsyncContextMap putAll(final AsyncContextMap context) {
            assert keyOne != null && keyTwo != null && keyThree != null;
            switch (context.size()) {
                case 0:
                    return this;
                case 1: {
                    PutFourBuilder builder = new PutFourBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree);
                    context.forEach(builder);
                    return builder.build();
                }
                case 2: {
                    PutFiveBuilder builder = new PutFiveBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree);
                    context.forEach(builder);
                    return builder.build();
                }
                case 3: {
                    PutSixBuilder builder = new PutSixBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree);
                    context.forEach(builder);
                    return builder.build();
                }
                default:
                    PutSevenBuilder builder = new PutSevenBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, context.size());
                    context.forEach(builder);
                    return builder.build();
            }
        }

        @Override
        public AsyncContextMap putAll(final Map<Key<?>, Object> map) {
            assert keyOne != null && keyTwo != null && keyThree != null;
            switch (map.size()) {
                case 0:
                    return this;
                case 1: {
                    PutFourBuilder builder = new PutFourBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree);
                    map.forEach(builder);
                    return builder.build();
                }
                case 2: {
                    PutFiveBuilder builder = new PutFiveBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree);
                    map.forEach(builder);
                    return builder.build();
                }
                case 3: {
                    PutSixBuilder builder = new PutSixBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree);
                    map.forEach(builder);
                    return builder.build();
                }
                default:
                    PutSevenBuilder builder = new PutSevenBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, map.size());
                    map.forEach(builder);
                    return builder.build();
            }
        }

        @Override
        public AsyncContextMap remove(final Key<?> key) {
            assert keyOne != null && keyTwo != null && keyThree != null;
            return keyOne.equals(key) ? new TwoAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree) :
                    keyTwo.equals(key) ? new TwoAsyncContextMap(keyOne, valueOne, keyThree, valueThree) :
                            keyThree.equals(key) ? new TwoAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo) : this;
        }

        @Override
        public AsyncContextMap removeAll(final AsyncContextMap context) {
            assert keyOne != null && keyTwo != null && keyThree != null;
            MutableInt removeIndexMask = new MutableInt();
            context.forEach((k, v) -> {
                if (keyOne.equals(k)) {
                    removeIndexMask.value |= 0x1;
                    return removeIndexMask.value != 0x7;
                } else if (keyTwo.equals(k)) {
                    removeIndexMask.value |= 0x2;
                    return removeIndexMask.value != 0x7;
                } else if (keyThree.equals(k)) {
                    removeIndexMask.value |= 0x4;
                    return removeIndexMask.value != 0x7;
                }
                return true;
            });
            return removeAll(removeIndexMask);
        }

        @Override
        public AsyncContextMap removeAll(final Iterable<Key<?>> entries) {
            assert keyOne != null && keyTwo != null && keyThree != null;
            MutableInt removeIndexMask = new MutableInt();
            entries.forEach(k -> {
                if (keyOne.equals(k)) {
                    removeIndexMask.value |= 0x1;
                } else if (keyTwo.equals(k)) {
                    removeIndexMask.value |= 0x2;
                } else if (keyThree.equals(k)) {
                    removeIndexMask.value |= 0x4;
                }
            });
            return removeAll(removeIndexMask);
        }

        private AsyncContextMap removeAll(MutableInt removeIndexMask) {
            assert keyOne != null && keyTwo != null && keyThree != null;
            if ((removeIndexMask.value & 0x7) == 0x7) {
                return EMPTY_CONTEXT_MAP;
            } else if ((removeIndexMask.value & 0x4) == 0x4) {
                if ((removeIndexMask.value & 0x2) == 0x2) {
                    return new OneAsyncContextMap(keyOne, valueOne);
                } else if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new OneAsyncContextMap(keyTwo, valueTwo);
                }
                return new TwoAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo);
            } else if ((removeIndexMask.value & 0x2) == 0x2) {
                if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new OneAsyncContextMap(keyThree, valueThree);
                }
                return new TwoAsyncContextMap(keyOne, valueOne, keyThree, valueThree);
            } else if ((removeIndexMask.value & 0x1) == 0x1) {
                return new TwoAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree);
            }
            return this;
        }

        @Override
        public AsyncContextMap clear() {
            return EMPTY_CONTEXT_MAP;
        }

        @Nullable
        @Override
        public Key<?> forEach(final BiPredicate<Key<?>, Object> consumer) {
            if (!consumer.test(keyOne, valueOne)) {
                return keyOne;
            }
            if (!consumer.test(keyTwo, valueTwo)) {
                return keyTwo;
            }
            return consumer.test(keyThree, valueThree) ? null : keyThree;
        }

        @Override
        public void accept(final Key<?> key, final Object value) {
            test(key, value);
        }

        @Override
        public boolean test(final Key<?> key, final Object value) {
            if (keyOne == null) {
                keyOne = key;
                valueOne = value;
            } else if (keyTwo == null) {
                keyTwo = key;
                valueTwo = value;
            } else {
                assert keyThree == null;
                keyThree = key;
                valueThree = value;
                return false;
            }
            return true;
        }

        private static final class PutFourBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            private final Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            private final Key<?> keyThree;
            @Nullable
            private Object valueThree;
            @Nullable
            private Key<?> keyFour;
            @Nullable
            private Object valueFour;

            PutFourBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                           Key<?> keyThree, @Nullable Object valueThree) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
                this.keyTwo = keyTwo;
                this.valueTwo = valueTwo;
                this.keyThree = keyThree;
                this.valueThree = valueThree;
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo.equals(key)) {
                    valueTwo = o;
                } else if (keyThree.equals(key)) {
                    valueThree = o;
                } else {
                    assert keyFour == null;
                    keyFour = key;
                    valueFour = o;
                    return false;
                }
                return true;
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }

            AsyncContextMap build() {
                if (keyFour == null) {
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
                }
                return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour);
            }
        }

        private static final class PutFiveBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            private final Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            private final Key<?> keyThree;
            @Nullable
            private Object valueThree;
            @Nullable
            private Key<?> keyFour;
            @Nullable
            private Object valueFour;
            @Nullable
            private Key<?> keyFive;
            @Nullable
            private Object valueFive;

            PutFiveBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                           Key<?> keyThree, @Nullable Object valueThree) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
                this.keyTwo = keyTwo;
                this.valueTwo = valueTwo;
                this.keyThree = keyThree;
                this.valueThree = valueThree;
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo.equals(key)) {
                    valueTwo = o;
                } else if (keyThree.equals(key)) {
                    valueThree = o;
                } else if (keyFour == null) {
                    keyFour = key;
                    valueFour = o;
                } else {
                    assert keyFive == null;
                    keyFive = key;
                    valueFive = o;
                    return false;
                }
                return true;
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }

            AsyncContextMap build() {
                if (keyFour == null) {
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
                }
                if (keyFive == null) {
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour);
                }
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive);
            }
        }

        private static final class PutSixBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            private final Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            private final Key<?> keyThree;
            @Nullable
            private Object valueThree;
            @Nullable
            private Key<?> keyFour;
            @Nullable
            private Object valueFour;
            @Nullable
            private Key<?> keyFive;
            @Nullable
            private Object valueFive;
            @Nullable
            private Key<?> keySix;
            @Nullable
            private Object valueSix;

            PutSixBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                          Key<?> keyThree, @Nullable Object valueThree) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
                this.keyTwo = keyTwo;
                this.valueTwo = valueTwo;
                this.keyThree = keyThree;
                this.valueThree = valueThree;
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo.equals(key)) {
                    valueTwo = o;
                } else if (keyThree.equals(key)) {
                    valueThree = o;
                } else if (keyFour == null) {
                    keyFour = key;
                    valueFour = o;
                } else if (keyFive == null) {
                    keyFive = key;
                    valueFive = o;
                } else {
                    assert keySix == null;
                    keySix = key;
                    valueSix = o;
                    return false;
                }
                return true;
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }

            AsyncContextMap build() {
                if (keyFour == null) {
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
                }
                if (keyFive == null) {
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour);
                }
                if (keySix == null) {
                    return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour, keyFive, valueFive);
                }
                return new SixAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive, keySix, valueSix);
            }
        }

        private static final class PutSevenBuilder extends AbstractPutSevenBuilder {
            PutSevenBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                            Key<?> keyThree, @Nullable Object valueThree, int putAllMapSize) {
                super((3 + putAllMapSize) << 1, 6);
                pairs[0] = keyOne;
                pairs[1] = valueOne;
                pairs[2] = keyTwo;
                pairs[3] = valueTwo;
                pairs[4] = keyThree;
                pairs[5] = valueThree;
            }

            AsyncContextMap build() {
                if (nextIndex == 6) {
                    return new ThreeAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5]);
                }
                if (nextIndex == 8) {
                    return new FourAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7]);
                }
                if (nextIndex == 10) {
                    return new FiveAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7], (Key<?>) pairs[8], pairs[9]);
                }
                if (nextIndex == 12) {
                    return new SixAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7], (Key<?>) pairs[8], pairs[9],
                            (Key<?>) pairs[10], pairs[11]);
                }
                if (nextIndex == pairs.length) {
                    return new SevenOrMoreAsyncContextMap(pairs);
                }
                return new SevenOrMoreAsyncContextMap(Arrays.copyOf(pairs, nextIndex));
            }
        }
    }

    private static final class FourAsyncContextMap implements AsyncContextMap, BiConsumer<Key<?>, Object>,
                                                              BiPredicate<Key<?>, Object> {

        @Nullable
        private Key<?> keyOne;
        @Nullable
        private Object valueOne;
        @Nullable
        private Key<?> keyTwo;
        @Nullable
        private Object valueTwo;
        @Nullable
        private Key<?> keyThree;
        @Nullable
        private Object valueThree;
        @Nullable
        private Key<?> keyFour;
        @Nullable
        private Object valueFour;

        FourAsyncContextMap() {
        }

        FourAsyncContextMap(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                            Key<?> keyThree, @Nullable Object valueThree, Key<?> keyFour, @Nullable Object valueFour) {
            this.keyOne = keyOne;
            this.valueOne = valueOne;
            this.keyTwo = keyTwo;
            this.valueTwo = valueTwo;
            this.keyThree = keyThree;
            this.valueThree = valueThree;
            this.keyFour = keyFour;
            this.valueFour = valueFour;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T get(final Key<T> key) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null;
            return keyOne.equals(key) ? (T) valueOne : keyTwo.equals(key) ? (T) valueTwo : keyThree.equals(key) ?
                    (T) valueThree : keyFour.equals(key) ? (T) valueFour : null;
        }

        @Override
        public boolean contains(final Key<?> key) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null;
            return keyOne.equals(key) || keyTwo.equals(key) || keyThree.equals(key) || keyFour.equals(key);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 4;
        }

        @Override
        public <T> AsyncContextMap put(final Key<T> key, @Nullable final T value) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null;
            if (keyOne.equals(key)) {
                return new FourAsyncContextMap(keyOne, value, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour);
            } else if (keyTwo.equals(key)) {
                return new FourAsyncContextMap(keyOne, valueOne, keyTwo, value, keyThree, valueThree,
                        keyFour, valueFour);
            } else if (keyThree.equals(key)) {
                return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, value, keyFour, valueFour);
            } else if (keyFour.equals(key)) {
                return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, value);
            }
            return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour,
                    key, value);
        }

        @Override
        public AsyncContextMap putAll(final AsyncContextMap context) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null;
            switch (context.size()) {
                case 0:
                    return this;
                case 1: {
                    PutFiveBuilder builder = new PutFiveBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, keyFour, valueFour);
                    context.forEach(builder);
                    return builder.build();
                }
                case 2: {
                    PutSixBuilder builder = new PutSixBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, keyFour, valueFour);
                    context.forEach(builder);
                    return builder.build();
                }
                default:
                    PutSevenBuilder builder = new PutSevenBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, keyFour, valueFour, context.size());
                    context.forEach(builder);
                    return builder.build();
            }
        }

        @Override
        public AsyncContextMap putAll(final Map<Key<?>, Object> map) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null;
            switch (map.size()) {
                case 0:
                    return this;
                case 1: {
                    PutFiveBuilder builder = new PutFiveBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, keyFour, valueFour);
                    map.forEach(builder);
                    return builder.build();
                }
                case 2: {
                    PutSixBuilder builder = new PutSixBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, keyFour, valueFour);
                    map.forEach(builder);
                    return builder.build();
                }
                default:
                    PutSevenBuilder builder = new PutSevenBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, keyFour, valueFour, map.size());
                    map.forEach(builder);
                    return builder.build();
            }
        }

        @Override
        public AsyncContextMap remove(final Key<?> key) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null;
            return keyOne.equals(key) ?
                    new ThreeAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour) :
                    keyTwo.equals(key) ?
                        new ThreeAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFour, valueFour) :
                    keyThree.equals(key) ?
                        new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFour, valueFour) :
                    keyFour.equals(key) ?
                        new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree) : this;
        }

        @Override
        public AsyncContextMap removeAll(final AsyncContextMap context) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null;
            MutableInt removeIndexMask = new MutableInt();
            context.forEach((k, v) -> {
                if (keyOne.equals(k)) {
                    removeIndexMask.value |= 0x1;
                    return removeIndexMask.value != 0xf;
                } else if (keyTwo.equals(k)) {
                    removeIndexMask.value |= 0x2;
                    return removeIndexMask.value != 0xf;
                } else if (keyThree.equals(k)) {
                    removeIndexMask.value |= 0x4;
                    return removeIndexMask.value != 0xf;
                } else if (keyFour.equals(k)) {
                    removeIndexMask.value |= 0x8;
                    return removeIndexMask.value != 0xf;
                }
                return true;
            });
            return removeAll(removeIndexMask);
        }

        @Override
        public AsyncContextMap removeAll(final Iterable<Key<?>> entries) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null;
            MutableInt removeIndexMask = new MutableInt();
            entries.forEach(k -> {
                if (keyOne.equals(k)) {
                    removeIndexMask.value |= 0x1;
                } else if (keyTwo.equals(k)) {
                    removeIndexMask.value |= 0x2;
                } else if (keyThree.equals(k)) {
                    removeIndexMask.value |= 0x4;
                } else if (keyFour.equals(k)) {
                    removeIndexMask.value |= 0x8;
                }
            });
            return removeAll(removeIndexMask);
        }

        private AsyncContextMap removeAll(MutableInt removeIndexMask) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null;
            if ((removeIndexMask.value & 0xf) == 0xf) {
                return EMPTY_CONTEXT_MAP;
            } else if ((removeIndexMask.value & 0x8) == 0x8) {
                if ((removeIndexMask.value & 0x4) == 0x4) {
                    if ((removeIndexMask.value & 0x2) == 0x2) {
                        return new OneAsyncContextMap(keyOne, valueOne);
                    } else if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new OneAsyncContextMap(keyTwo, valueTwo);
                    }
                    return new TwoAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo);
                } else if ((removeIndexMask.value & 0x2) == 0x2) {
                    if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new OneAsyncContextMap(keyThree, valueThree);
                    }
                    return new TwoAsyncContextMap(keyOne, valueOne, keyThree, valueThree);
                } else if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new TwoAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree);
                }
                return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
            } else if ((removeIndexMask.value & 0x4) == 0x4) {
                if ((removeIndexMask.value & 0x2) == 0x2) {
                    if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new OneAsyncContextMap(keyFour, valueFour);
                    }
                    return new TwoAsyncContextMap(keyOne, valueOne, keyFour, valueFour);
                } else if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new TwoAsyncContextMap(keyTwo, valueTwo, keyFour, valueFour);
                }
                return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFour, valueFour);
            } else if ((removeIndexMask.value & 0x2) == 0x2) {
                if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new TwoAsyncContextMap(keyThree, valueThree, keyFour, valueFour);
                }
                return new ThreeAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFour, valueFour);
            } else if ((removeIndexMask.value & 0x1) == 0x1) {
                return new ThreeAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour);
            }
            return this;
        }

        @Override
        public AsyncContextMap clear() {
            return EMPTY_CONTEXT_MAP;
        }

        @Nullable
        @Override
        public Key<?> forEach(final BiPredicate<Key<?>, Object> consumer) {
            if (!consumer.test(keyOne, valueOne)) {
                return keyOne;
            }
            if (!consumer.test(keyTwo, valueTwo)) {
                return keyTwo;
            }
            if (!consumer.test(keyThree, valueThree)) {
                return keyThree;
            }
            return consumer.test(keyFour, valueFour) ? null : keyFour;
        }

        @Override
        public void accept(final Key<?> key, final Object value) {
            test(key, value);
        }

        @Override
        public boolean test(final Key<?> key, final Object value) {
            if (keyOne == null) {
                keyOne = key;
                valueOne = value;
            } else if (keyTwo == null) {
                keyTwo = key;
                valueTwo = value;
            } else if (keyThree == null) {
                keyThree = key;
                valueThree = value;
            } else {
                assert keyFour == null;
                keyFour = key;
                valueFour = value;
                return false;
            }
            return true;
        }

        private static final class PutFiveBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            private final Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            private final Key<?> keyThree;
            @Nullable
            private Object valueThree;
            private final Key<?> keyFour;
            @Nullable
            private Object valueFour;
            @Nullable
            private Key<?> keyFive;
            @Nullable
            private Object valueFive;

            PutFiveBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                           Key<?> keyThree, @Nullable Object valueThree, Key<?> keyFour, @Nullable Object valueFour) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
                this.keyTwo = keyTwo;
                this.valueTwo = valueTwo;
                this.keyThree = keyThree;
                this.valueThree = valueThree;
                this.keyFour = keyFour;
                this.valueFour = valueFour;
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo.equals(key)) {
                    valueTwo = o;
                } else if (keyThree.equals(key)) {
                    valueThree = o;
                } else if (keyFour.equals(key)) {
                    valueFour = o;
                } else {
                    assert keyFive == null;
                    keyFive = key;
                    valueFive = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                if (keyFive == null) {
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour);
                }
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive);
            }
        }

        private static final class PutSixBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            private final Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            private final Key<?> keyThree;
            @Nullable
            private Object valueThree;
            private final Key<?> keyFour;
            @Nullable
            private Object valueFour;
            @Nullable
            private Key<?> keyFive;
            @Nullable
            private Object valueFive;
            @Nullable
            private Key<?> keySix;
            @Nullable
            private Object valueSix;

            PutSixBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                          Key<?> keyThree, @Nullable Object valueThree, Key<?> keyFour, @Nullable Object valueFour) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
                this.keyTwo = keyTwo;
                this.valueTwo = valueTwo;
                this.keyThree = keyThree;
                this.valueThree = valueThree;
                this.keyFour = keyFour;
                this.valueFour = valueFour;
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo.equals(key)) {
                    valueTwo = o;
                } else if (keyThree.equals(key)) {
                    valueThree = o;
                } else if (keyFour.equals(key)) {
                    valueFour = o;
                } else if (keyFive == null) {
                    keyFive = key;
                    valueFive = o;
                } else {
                    assert keySix == null;
                    keySix = key;
                    valueSix = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                if (keyFive == null) {
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour);
                }
                if (keySix == null) {
                    return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour, keyFive, valueFive);
                }
                return new SixAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive, keySix, valueSix);
            }
        }

        private static final class PutSevenBuilder extends AbstractPutSevenBuilder {
            PutSevenBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                            Key<?> keyThree, @Nullable Object valueThree, Key<?> keyFour, @Nullable Object valueFour,
                            int putAllMapSize) {
                super((4 + putAllMapSize) << 1, 8);
                pairs[0] = keyOne;
                pairs[1] = valueOne;
                pairs[2] = keyTwo;
                pairs[3] = valueTwo;
                pairs[4] = keyThree;
                pairs[5] = valueThree;
                pairs[6] = keyFour;
                pairs[7] = valueFour;
            }

            AsyncContextMap build() {
                if (nextIndex == 8) {
                    return new FourAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7]);
                }
                if (nextIndex == 10) {
                    return new FiveAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7], (Key<?>) pairs[8], pairs[9]);
                }
                if (nextIndex == 12) {
                    return new SixAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7], (Key<?>) pairs[8], pairs[9],
                            (Key<?>) pairs[10], pairs[11]);
                }
                if (nextIndex == pairs.length) {
                    return new SevenOrMoreAsyncContextMap(pairs);
                }
                return new SevenOrMoreAsyncContextMap(Arrays.copyOf(pairs, nextIndex));
            }
        }
    }

    private static final class FiveAsyncContextMap implements AsyncContextMap, BiConsumer<Key<?>, Object>,
                                                              BiPredicate<Key<?>, Object> {

        @Nullable
        private Key<?> keyOne;
        @Nullable
        private Object valueOne;
        @Nullable
        private Key<?> keyTwo;
        @Nullable
        private Object valueTwo;
        @Nullable
        private Key<?> keyThree;
        @Nullable
        private Object valueThree;
        @Nullable
        private Key<?> keyFour;
        @Nullable
        private Object valueFour;
        @Nullable
        private Key<?> keyFive;
        @Nullable
        private Object valueFive;

        FiveAsyncContextMap() {
        }

        FiveAsyncContextMap(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                            Key<?> keyThree, @Nullable Object valueThree, Key<?> keyFour, @Nullable Object valueFour,
                            Key<?> keyFive, @Nullable Object valueFive) {
            this.keyOne = keyOne;
            this.valueOne = valueOne;
            this.keyTwo = keyTwo;
            this.valueTwo = valueTwo;
            this.keyThree = keyThree;
            this.valueThree = valueThree;
            this.keyFour = keyFour;
            this.valueFour = valueFour;
            this.keyFive = keyFive;
            this.valueFive = valueFive;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T get(final Key<T> key) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null;
            return keyOne.equals(key) ? (T) valueOne : keyTwo.equals(key) ? (T) valueTwo : keyThree.equals(key) ?
                    (T) valueThree : keyFour.equals(key) ? (T) valueFour : keyFive.equals(key) ? (T) valueFive : null;
        }

        @Override
        public boolean contains(final Key<?> key) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null;
            return keyOne.equals(key) || keyTwo.equals(key) || keyThree.equals(key) || keyFour.equals(key) ||
                    keyFive.equals(key);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 5;
        }

        @Override
        public <T> AsyncContextMap put(final Key<T> key, @Nullable final T value) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null;
            if (keyOne.equals(key)) {
                return new FiveAsyncContextMap(keyOne, value, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive);
            } else if (keyTwo.equals(key)) {
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, value, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive);
            } else if (keyThree.equals(key)) {
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, value, keyFour, valueFour,
                        keyFive, valueFive);
            } else if (keyFour.equals(key)) {
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, value, keyFive, valueFive);
            } else if (keyFive.equals(key)) {
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, value);
            }
            return new SixAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour,
                    keyFive, valueFive, key, value);
        }

        @Override
        public AsyncContextMap putAll(final AsyncContextMap context) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null;
            switch (context.size()) {
                case 0:
                    return this;
                case 1: {
                    PutSixBuilder builder = new PutSixBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, keyFour, valueFour, keyFive, valueFive);
                    context.forEach(builder);
                    return builder.build();
                }
                default:
                    PutSevenBuilder builder = new PutSevenBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, keyFour, valueFour, keyFive, valueFive, context.size());
                    context.forEach(builder);
                    return builder.build();
            }
        }

        @Override
        public AsyncContextMap putAll(final Map<Key<?>, Object> map) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null;
            switch (map.size()) {
                case 0:
                    return this;
                case 1: {
                    PutSixBuilder builder = new PutSixBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, keyFour, valueFour, keyFive, valueFive);
                    map.forEach(builder);
                    return builder.build();
                }
                default:
                    PutSevenBuilder builder = new PutSevenBuilder(keyOne, valueOne, keyTwo, valueTwo,
                            keyThree, valueThree, keyFour, valueFour, keyFive, valueFive, map.size());
                    map.forEach(builder);
                    return builder.build();
            }
        }

        @Override
        public AsyncContextMap remove(final Key<?> key) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null;
            return keyOne.equals(key) ?
                        new FourAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour,
                            keyFive, valueFive) :
                    keyTwo.equals(key) ?
                        new FourAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFour, valueFour,
                            keyFive, valueFive) :
                    keyThree.equals(key) ?
                        new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFour, valueFour,
                            keyFive, valueFive) :
                    keyFour.equals(key) ?
                        new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFive, valueFive) :
                    keyFive.equals(key) ?
                        new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour) : this;
        }

        @Override
        public AsyncContextMap removeAll(final AsyncContextMap context) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null;
            MutableInt removeIndexMask = new MutableInt();
            context.forEach((k, v) -> {
                if (keyOne.equals(k)) {
                    removeIndexMask.value |= 0x1;
                    return removeIndexMask.value != 0x1f;
                } else if (keyTwo.equals(k)) {
                    removeIndexMask.value |= 0x2;
                    return removeIndexMask.value != 0x1f;
                } else if (keyThree.equals(k)) {
                    removeIndexMask.value |= 0x4;
                    return removeIndexMask.value != 0x1f;
                } else if (keyFour.equals(k)) {
                    removeIndexMask.value |= 0x8;
                    return removeIndexMask.value != 0x1f;
                } else if (keyFive.equals(k)) {
                    removeIndexMask.value |= 0x10;
                    return removeIndexMask.value != 0x1f;
                }
                return true;
            });
            return removeAll(removeIndexMask);
        }

        @Override
        public AsyncContextMap removeAll(final Iterable<Key<?>> entries) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null;
            MutableInt removeIndexMask = new MutableInt();
            entries.forEach(k -> {
                if (keyOne.equals(k)) {
                    removeIndexMask.value |= 0x1;
                } else if (keyTwo.equals(k)) {
                    removeIndexMask.value |= 0x2;
                } else if (keyThree.equals(k)) {
                    removeIndexMask.value |= 0x4;
                } else if (keyFour.equals(k)) {
                    removeIndexMask.value |= 0x8;
                } else if (keyFive.equals(k)) {
                    removeIndexMask.value |= 0x10;
                }
            });
            return removeAll(removeIndexMask);
        }

        private AsyncContextMap removeAll(MutableInt removeIndexMask) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null;
            if ((removeIndexMask.value & 0x1f) == 0x1f) {
                return EMPTY_CONTEXT_MAP;
            } else if ((removeIndexMask.value & 0x10) == 0x10) {
                if ((removeIndexMask.value & 0x8) == 0x8) {
                    if ((removeIndexMask.value & 0x4) == 0x4) {
                        if ((removeIndexMask.value & 0x2) == 0x2) {
                            return new OneAsyncContextMap(keyOne, valueOne);
                        } else if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new OneAsyncContextMap(keyTwo, valueTwo);
                        }
                        return new TwoAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo);
                    } else if ((removeIndexMask.value & 0x2) == 0x2) {
                        if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new OneAsyncContextMap(keyThree, valueThree);
                        }
                        return new TwoAsyncContextMap(keyOne, valueOne, keyThree, valueThree);
                    } else if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new TwoAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree);
                    }
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
                } else if ((removeIndexMask.value & 0x4) == 0x4) {
                    if ((removeIndexMask.value & 0x2) == 0x2) {
                        if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new OneAsyncContextMap(keyFour, valueFour);
                        }
                        return new TwoAsyncContextMap(keyOne, valueOne, keyFour, valueFour);
                    } else if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new TwoAsyncContextMap(keyTwo, valueTwo, keyFour, valueFour);
                    }
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFour, valueFour);
                } else if ((removeIndexMask.value & 0x2) == 0x2) {
                    if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new TwoAsyncContextMap(keyThree, valueThree, keyFour, valueFour);
                    }
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFour, valueFour);
                } else if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new ThreeAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour);
                }
                return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour);
            } else if ((removeIndexMask.value & 0x8) == 0x8) {
                if ((removeIndexMask.value & 0x4) == 0x4) {
                    if ((removeIndexMask.value & 0x2) == 0x2) {
                        if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new OneAsyncContextMap(keyFive, valueFive);
                        }
                        return new TwoAsyncContextMap(keyOne, valueOne, keyFive, valueFive);
                    } else if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new TwoAsyncContextMap(keyTwo, valueTwo, keyFive, valueFive);
                    }
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFive, valueFive);
                } else if ((removeIndexMask.value & 0x2) == 0x2) {
                    if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new TwoAsyncContextMap(keyThree, valueThree, keyFive, valueFive);
                    }
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFive, valueFive);
                } else if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new ThreeAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFive, valueFive);
                }
                return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFive, valueFive);
            } else if ((removeIndexMask.value & 0x4) == 0x4) {
                if ((removeIndexMask.value & 0x2) == 0x2) {
                    if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new TwoAsyncContextMap(keyFour, valueFour, keyFive, valueFive);
                    }
                    return new ThreeAsyncContextMap(keyOne, valueOne, keyFour, valueFour, keyFive, valueFive);
                } else if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new ThreeAsyncContextMap(keyTwo, valueTwo, keyFour, valueFour, keyFive, valueFive);
                }
                return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFour, valueFour,
                        keyFive, valueFive);
            } else if ((removeIndexMask.value & 0x2) == 0x2) {
                if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new ThreeAsyncContextMap(keyThree, valueThree, keyFour, valueFour,
                            keyFive, valueFive);
                }
                return new FourAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFour, valueFour,
                        keyFive, valueFive);
            } else if ((removeIndexMask.value & 0x1) == 0x1) {
                return new FourAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour,
                        keyFive, valueFive);
            }
            return this;
        }

        @Override
        public AsyncContextMap clear() {
            return EMPTY_CONTEXT_MAP;
        }

        @Nullable
        @Override
        public Key<?> forEach(final BiPredicate<Key<?>, Object> consumer) {
            if (!consumer.test(keyOne, valueOne)) {
                return keyOne;
            }
            if (!consumer.test(keyTwo, valueTwo)) {
                return keyTwo;
            }
            if (!consumer.test(keyThree, valueThree)) {
                return keyThree;
            }
            if (!consumer.test(keyFour, valueFour)) {
                return keyFour;
            }
            return consumer.test(keyFive, valueFive) ? null : keyFive;
        }

        @Override
        public void accept(final Key<?> key, final Object value) {
            test(key, value);
        }

        @Override
        public boolean test(final Key<?> key, final Object value) {
            if (keyOne == null) {
                keyOne = key;
                valueOne = value;
            } else if (keyTwo == null) {
                keyTwo = key;
                valueTwo = value;
            } else if (keyThree == null) {
                keyThree = key;
                valueThree = value;
            } else if (keyFour == null) {
                keyFour = key;
                valueFour = value;
            } else {
                assert keyFive == null;
                keyFive = key;
                valueFive = value;
                return false;
            }
            return true;
        }

        private static final class PutSixBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
            private final Key<?> keyOne;
            @Nullable
            private Object valueOne;
            private final Key<?> keyTwo;
            @Nullable
            private Object valueTwo;
            private final Key<?> keyThree;
            @Nullable
            private Object valueThree;
            private final Key<?> keyFour;
            @Nullable
            private Object valueFour;
            private final Key<?> keyFive;
            @Nullable
            private Object valueFive;
            @Nullable
            private Key<?> keySix;
            @Nullable
            private Object valueSix;

            PutSixBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                          Key<?> keyThree, @Nullable Object valueThree, Key<?> keyFour, @Nullable Object valueFour,
                          Key<?> keyFive, @Nullable Object valueFive) {
                this.keyOne = keyOne;
                this.valueOne = valueOne;
                this.keyTwo = keyTwo;
                this.valueTwo = valueTwo;
                this.keyThree = keyThree;
                this.valueThree = valueThree;
                this.keyFour = keyFour;
                this.valueFour = valueFour;
                this.keyFive = keyFive;
                this.valueFive = valueFive;
            }

            @Override
            public void accept(final Key<?> key, final Object o) {
                test(key, o);
            }

            @Override
            public boolean test(final Key<?> key, final Object o) {
                if (keyOne.equals(key)) {
                    valueOne = o;
                } else if (keyTwo.equals(key)) {
                    valueTwo = o;
                } else if (keyThree.equals(key)) {
                    valueThree = o;
                } else if (keyFour.equals(key)) {
                    valueFour = o;
                } else if (keyFive.equals(key)) {
                    valueFive = o;
                } else {
                    assert keySix == null;
                    keySix = key;
                    valueSix = o;
                    return false;
                }
                return true;
            }

            AsyncContextMap build() {
                if (keySix == null) {
                    return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour, keyFive, valueFive);
                }
                return new SixAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive, keySix, valueSix);
            }
        }

        private static final class PutSevenBuilder extends AbstractPutSevenBuilder {
            PutSevenBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                            Key<?> keyThree, @Nullable Object valueThree, Key<?> keyFour, @Nullable Object valueFour,
                            Key<?> keyFive, @Nullable Object valueFive, int putAllMapSize) {
                super((5 + putAllMapSize) << 1, 10);
                pairs[0] = keyOne;
                pairs[1] = valueOne;
                pairs[2] = keyTwo;
                pairs[3] = valueTwo;
                pairs[4] = keyThree;
                pairs[5] = valueThree;
                pairs[6] = keyFour;
                pairs[7] = valueFour;
                pairs[8] = keyFive;
                pairs[9] = valueFive;
            }

            AsyncContextMap build() {
                if (nextIndex == 10) {
                    return new FiveAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7], (Key<?>) pairs[8], pairs[9]);
                }
                if (nextIndex == 12) {
                    return new SixAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7], (Key<?>) pairs[8], pairs[9],
                            (Key<?>) pairs[10], pairs[11]);
                }
                if (nextIndex == pairs.length) {
                    return new SevenOrMoreAsyncContextMap(pairs);
                }
                return new SevenOrMoreAsyncContextMap(Arrays.copyOf(pairs, nextIndex));
            }
        }
    }

    private static final class SixAsyncContextMap implements AsyncContextMap, BiConsumer<Key<?>, Object>,
                                                             BiPredicate<Key<?>, Object> {

        @Nullable
        private Key<?> keyOne;
        @Nullable
        private Object valueOne;
        @Nullable
        private Key<?> keyTwo;
        @Nullable
        private Object valueTwo;
        @Nullable
        private Key<?> keyThree;
        @Nullable
        private Object valueThree;
        @Nullable
        private Key<?> keyFour;
        @Nullable
        private Object valueFour;
        @Nullable
        private Key<?> keyFive;
        @Nullable
        private Object valueFive;
        @Nullable
        private Key<?> keySix;
        @Nullable
        private Object valueSix;

        SixAsyncContextMap() {
        }

        SixAsyncContextMap(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                           Key<?> keyThree, @Nullable Object valueThree, Key<?> keyFour, @Nullable Object valueFour,
                           Key<?> keyFive, @Nullable Object valueFive, Key<?> keySix, @Nullable Object valueSix) {
            this.keyOne = keyOne;
            this.valueOne = valueOne;
            this.keyTwo = keyTwo;
            this.valueTwo = valueTwo;
            this.keyThree = keyThree;
            this.valueThree = valueThree;
            this.keyFour = keyFour;
            this.valueFour = valueFour;
            this.keyFive = keyFive;
            this.valueFive = valueFive;
            this.keySix = keySix;
            this.valueSix = valueSix;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T get(final Key<T> key) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null &&
                    keySix != null;
            return keyOne.equals(key) ? (T) valueOne : keyTwo.equals(key) ? (T) valueTwo : keyThree.equals(key) ?
                    (T) valueThree : keyFour.equals(key) ? (T) valueFour : keyFive.equals(key) ? (T) valueFive :
                    keySix.equals(key) ? (T) valueSix : null;
        }

        @Override
        public boolean contains(final Key<?> key) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null &&
                    keySix != null;
            return keyOne.equals(key) || keyTwo.equals(key) || keyThree.equals(key) || keyFour.equals(key) ||
                    keyFive.equals(key) || keySix.equals(key);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 6;
        }

        @Override
        public <T> AsyncContextMap put(final Key<T> key, @Nullable final T value) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null &&
                    keySix != null;
            if (keyOne.equals(key)) {
                return new SixAsyncContextMap(keyOne, value, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive, keySix, valueSix);
            } else if (keyTwo.equals(key)) {
                return new SixAsyncContextMap(keyOne, valueOne, keyTwo, value, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive, keySix, valueSix);
            } else if (keyThree.equals(key)) {
                return new SixAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, value, keyFour, valueFour,
                        keyFive, valueFive, keySix, valueSix);
            } else if (keyFour.equals(key)) {
                return new SixAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, value, keyFive, valueFive, keySix, valueSix);
            } else if (keyFive.equals(key)) {
                return new SixAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, value, keySix, valueSix);
            } else if (keySix.equals(key)) {
                return new SixAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive, keySix, value);
            }
            return new SevenOrMoreAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                    keyFour, valueFour, keyFive, valueFive, keySix, valueSix, key, value);
        }

        @Override
        public AsyncContextMap putAll(final AsyncContextMap context) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null &&
                    keySix != null;
            if (context.isEmpty()) {
                return this;
            }
            PutSevenBuilder builder = new PutSevenBuilder(keyOne, valueOne, keyTwo, valueTwo,
                    keyThree, valueThree, keyFour, valueFour, keyFive, valueFive, keySix, valueSix, context.size());
            context.forEach(builder);
            return builder.build();
        }

        @Override
        public AsyncContextMap putAll(final Map<Key<?>, Object> map) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null &&
                    keySix != null;
            if (map.isEmpty()) {
                return this;
            }
            PutSevenBuilder builder = new PutSevenBuilder(keyOne, valueOne, keyTwo, valueTwo,
                    keyThree, valueThree, keyFour, valueFour, keyFive, valueFive, keySix, valueSix, map.size());
            map.forEach(builder);
            return builder.build();
        }

        @Override
        public AsyncContextMap remove(final Key<?> key) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null &&
                    keySix != null;
            return keyOne.equals(key) ?
                        new FiveAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour,
                            keyFive, valueFive, keySix, valueSix) :
                    keyTwo.equals(key) ?
                        new FiveAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFour, valueFour,
                            keyFive, valueFive, keySix, valueSix) :
                    keyThree.equals(key) ?
                        new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFour, valueFour,
                            keyFive, valueFive, keySix, valueSix) :
                    keyFour.equals(key) ?
                        new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFive, valueFive, keySix, valueSix) :
                    keyFive.equals(key) ?
                        new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour, keySix, valueSix) :
                    keySix.equals(key) ?
                        new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour, keyFive, valueFive) : this;
        }

        @Override
        public AsyncContextMap removeAll(final AsyncContextMap context) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null &&
                    keySix != null;
            MutableInt removeIndexMask = new MutableInt();
            context.forEach((k, v) -> {
                if (keyOne.equals(k)) {
                    removeIndexMask.value |= 0x1;
                    return removeIndexMask.value != 0x3f;
                } else if (keyTwo.equals(k)) {
                    removeIndexMask.value |= 0x2;
                    return removeIndexMask.value != 0x3f;
                } else if (keyThree.equals(k)) {
                    removeIndexMask.value |= 0x4;
                    return removeIndexMask.value != 0x3f;
                } else if (keyFour.equals(k)) {
                    removeIndexMask.value |= 0x8;
                    return removeIndexMask.value != 0x3f;
                } else if (keyFive.equals(k)) {
                    removeIndexMask.value |= 0x10;
                    return removeIndexMask.value != 0x3f;
                } else if (keySix.equals(k)) {
                    removeIndexMask.value |= 0x20;
                    return removeIndexMask.value != 0x3f;
                }
                return true;
            });
            return removeAll(removeIndexMask);
        }

        @Override
        public AsyncContextMap removeAll(final Iterable<Key<?>> entries) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null &&
                    keySix != null;
            MutableInt removeIndexMask = new MutableInt();
            entries.forEach(k -> {
                if (keyOne.equals(k)) {
                    removeIndexMask.value |= 0x1;
                } else if (keyTwo.equals(k)) {
                    removeIndexMask.value |= 0x2;
                } else if (keyThree.equals(k)) {
                    removeIndexMask.value |= 0x4;
                } else if (keyFour.equals(k)) {
                    removeIndexMask.value |= 0x8;
                } else if (keyFive.equals(k)) {
                    removeIndexMask.value |= 0x10;
                } else if (keySix.equals(k)) {
                    removeIndexMask.value |= 0x20;
                }
            });
            return removeAll(removeIndexMask);
        }

        private AsyncContextMap removeAll(MutableInt removeIndexMask) {
            assert keyOne != null && keyTwo != null && keyThree != null && keyFour != null && keyFive != null &&
                    keySix != null;
            if ((removeIndexMask.value & 0x3f) == 0x3f) {
                return EMPTY_CONTEXT_MAP;
            } else if ((removeIndexMask.value & 0x20) == 0x20) {
                if ((removeIndexMask.value & 0x10) == 0x10) {
                    if ((removeIndexMask.value & 0x8) == 0x8) {
                        if ((removeIndexMask.value & 0x4) == 0x4) {
                            if ((removeIndexMask.value & 0x2) == 0x2) {
                                return new OneAsyncContextMap(keyOne, valueOne);
                            } else if ((removeIndexMask.value & 0x1) == 0x1) {
                                return new OneAsyncContextMap(keyTwo, valueTwo);
                            }
                            return new TwoAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo);
                        } else if ((removeIndexMask.value & 0x2) == 0x2) {
                            if ((removeIndexMask.value & 0x1) == 0x1) {
                                return new OneAsyncContextMap(keyThree, valueThree);
                            }
                            return new TwoAsyncContextMap(keyOne, valueOne, keyThree, valueThree);
                        } else if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new TwoAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree);
                        }
                        return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree);
                    } else if ((removeIndexMask.value & 0x4) == 0x4) {
                        if ((removeIndexMask.value & 0x2) == 0x2) {
                            if ((removeIndexMask.value & 0x1) == 0x1) {
                                return new OneAsyncContextMap(keyFour, valueFour);
                            }
                            return new TwoAsyncContextMap(keyOne, valueOne, keyFour, valueFour);
                        } else if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new TwoAsyncContextMap(keyTwo, valueTwo, keyFour, valueFour);
                        }
                        return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFour, valueFour);
                    } else if ((removeIndexMask.value & 0x2) == 0x2) {
                        if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new TwoAsyncContextMap(keyThree, valueThree, keyFour, valueFour);
                        }
                        return new ThreeAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFour, valueFour);
                    } else if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new ThreeAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour);
                    }
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFour, valueFour);
                } else if ((removeIndexMask.value & 0x8) == 0x8) {
                    if ((removeIndexMask.value & 0x4) == 0x4) {
                        if ((removeIndexMask.value & 0x2) == 0x2) {
                            if ((removeIndexMask.value & 0x1) == 0x1) {
                                return new OneAsyncContextMap(keyFive, valueFive);
                            }
                            return new TwoAsyncContextMap(keyOne, valueOne, keyFive, valueFive);
                        } else if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new TwoAsyncContextMap(keyTwo, valueTwo, keyFive, valueFive);
                        }
                        return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFive, valueFive);
                    } else if ((removeIndexMask.value & 0x2) == 0x2) {
                        if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new TwoAsyncContextMap(keyThree, valueThree, keyFive, valueFive);
                        }
                        return new ThreeAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFive, valueFive);
                    } else if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new ThreeAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFive, valueFive);
                    }
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keyFive, valueFive);
                } else if ((removeIndexMask.value & 0x4) == 0x4) {
                    if ((removeIndexMask.value & 0x2) == 0x2) {
                        if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new TwoAsyncContextMap(keyFour, valueFour, keyFive, valueFive);
                        }
                        return new ThreeAsyncContextMap(keyOne, valueOne, keyFour, valueFour, keyFive, valueFive);
                    } else if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new ThreeAsyncContextMap(keyTwo, valueTwo, keyFour, valueFour, keyFive, valueFive);
                    }
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFour, valueFour,
                            keyFive, valueFive);
                } else if ((removeIndexMask.value & 0x2) == 0x2) {
                    if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new ThreeAsyncContextMap(keyThree, valueThree, keyFour, valueFour, keyFive, valueFive);
                    }
                    return new FourAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFour, valueFour,
                            keyFive, valueFive);
                } else if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new FourAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour,
                            keyFive, valueFive);
                }
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keyFive, valueFive);
            } else if ((removeIndexMask.value & 0x10) == 0x10) {
                if ((removeIndexMask.value & 0x8) == 0x8) {
                    if ((removeIndexMask.value & 0x4) == 0x4) {
                        if ((removeIndexMask.value & 0x2) == 0x2) {
                            if ((removeIndexMask.value & 0x1) == 0x1) {
                                return new OneAsyncContextMap(keySix, valueSix);
                            }
                            return new TwoAsyncContextMap(keyOne, valueOne, keySix, valueSix);
                        } else if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new TwoAsyncContextMap(keyTwo, valueTwo, keySix, valueSix);
                        }
                        return new ThreeAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keySix, valueSix);
                    } else if ((removeIndexMask.value & 0x2) == 0x2) {
                        if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new TwoAsyncContextMap(keyThree, valueThree, keySix, valueSix);
                        }
                        return new ThreeAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keySix, valueSix);
                    } else if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new ThreeAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keySix, valueSix);
                    }
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                            keySix, valueSix);
                } else if ((removeIndexMask.value & 0x4) == 0x4) {
                    if ((removeIndexMask.value & 0x2) == 0x2) {
                        if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new TwoAsyncContextMap(keyFour, valueFour, keySix, valueSix);
                        }
                        return new ThreeAsyncContextMap(keyOne, valueOne, keyFour, valueFour, keySix, valueSix);
                    } else if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new ThreeAsyncContextMap(keyTwo, valueTwo, keyFour, valueFour, keySix, valueSix);
                    }
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFour, valueFour,
                            keySix, valueSix);
                } else if ((removeIndexMask.value & 0x2) == 0x2) {
                    if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new ThreeAsyncContextMap(keyThree, valueThree, keyFour, valueFour, keySix, valueSix);
                    }
                    return new FourAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFour, valueFour,
                            keySix, valueSix);
                } else if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new FourAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour,
                            keySix, valueSix);
                }
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFour, valueFour, keySix, valueSix);
            } else if ((removeIndexMask.value & 0x8) == 0x8) {
                if ((removeIndexMask.value & 0x4) == 0x4) {
                    if ((removeIndexMask.value & 0x2) == 0x2) {
                        if ((removeIndexMask.value & 0x1) == 0x1) {
                            return new TwoAsyncContextMap(keyFive, valueFive, keySix, valueSix);
                        }
                        return new ThreeAsyncContextMap(keyOne, valueOne, keyFive, valueFive, keySix, valueSix);
                    } else if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new ThreeAsyncContextMap(keyTwo, valueTwo, keyFive, valueFive, keySix, valueSix);
                    }
                    return new FourAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFive, valueFive,
                            keySix, valueSix);
                } else if ((removeIndexMask.value & 0x2) == 0x2) {
                    if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new ThreeAsyncContextMap(keyThree, valueThree, keyFive, valueFive, keySix, valueSix);
                    }
                    return new FourAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFive, valueFive,
                            keySix, valueSix);
                } else if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new FourAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFive, valueFive,
                            keySix, valueSix);
                }
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyThree, valueThree,
                        keyFive, valueFive, keySix, valueSix);
            } else if ((removeIndexMask.value & 0x4) == 0x4) {
                if ((removeIndexMask.value & 0x2) == 0x2) {
                    if ((removeIndexMask.value & 0x1) == 0x1) {
                        return new ThreeAsyncContextMap(keyFour, valueFour, keyFive, valueFive, keySix, valueSix);
                    }
                    return new FourAsyncContextMap(keyOne, valueOne, keyFour, valueFour, keyFive, valueFive,
                            keySix, valueSix);
                } else if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new FourAsyncContextMap(keyTwo, valueTwo, keyFour, valueFour, keyFive, valueFive,
                            keySix, valueSix);
                }
                return new FiveAsyncContextMap(keyOne, valueOne, keyTwo, valueTwo, keyFour, valueFour,
                        keyFive, valueFive, keySix, valueSix);
            } else if ((removeIndexMask.value & 0x2) == 0x2) {
                if ((removeIndexMask.value & 0x1) == 0x1) {
                    return new FourAsyncContextMap(keyThree, valueThree, keyFour, valueFour, keyFive, valueFive,
                            keySix, valueSix);
                }
                return new FiveAsyncContextMap(keyOne, valueOne, keyThree, valueThree, keyFour, valueFour,
                        keyFive, valueFive, keySix, valueSix);
            } else if ((removeIndexMask.value & 0x1) == 0x1) {
                return new FiveAsyncContextMap(keyTwo, valueTwo, keyThree, valueThree, keyFour, valueFour,
                        keyFive, valueFive, keySix, valueSix);
            }
            return this;
        }

        @Override
        public AsyncContextMap clear() {
            return EMPTY_CONTEXT_MAP;
        }

        @Nullable
        @Override
        public Key<?> forEach(final BiPredicate<Key<?>, Object> consumer) {
            if (!consumer.test(keyOne, valueOne)) {
                return keyOne;
            }
            if (!consumer.test(keyTwo, valueTwo)) {
                return keyTwo;
            }
            if (!consumer.test(keyThree, valueThree)) {
                return keyThree;
            }
            if (!consumer.test(keyFour, valueFour)) {
                return keyFour;
            }
            if (!consumer.test(keyFive, valueFive)) {
                return keyFive;
            }
            return consumer.test(keySix, valueSix) ? null : keySix;
        }

        @Override
        public void accept(final Key<?> key, final Object value) {
            test(key, value);
        }

        @Override
        public boolean test(final Key<?> key, final Object value) {
            if (keyOne == null) {
                keyOne = key;
                valueOne = value;
            } else if (keyTwo == null) {
                keyTwo = key;
                valueTwo = value;
            } else if (keyThree == null) {
                keyThree = key;
                valueThree = value;
            } else if (keyFour == null) {
                keyFour = key;
                valueFour = value;
            } else if (keyFive == null) {
                keyFive = key;
                valueFive = value;
            } else {
                assert keySix == null;
                keySix = key;
                valueSix = value;
                return false;
            }
            return true;
        }

        private static final class PutSevenBuilder extends AbstractPutSevenBuilder {
            PutSevenBuilder(Key<?> keyOne, @Nullable Object valueOne, Key<?> keyTwo, @Nullable Object valueTwo,
                            Key<?> keyThree, @Nullable Object valueThree, Key<?> keyFour, @Nullable Object valueFour,
                            Key<?> keyFive, @Nullable Object valueFive, Key<?> keySix, @Nullable Object valueSix,
                            int putAllMapSize) {
                super((6 + putAllMapSize) << 1, 12);
                pairs[0] = keyOne;
                pairs[1] = valueOne;
                pairs[2] = keyTwo;
                pairs[3] = valueTwo;
                pairs[4] = keyThree;
                pairs[5] = valueThree;
                pairs[6] = keyFour;
                pairs[7] = valueFour;
                pairs[8] = keyFive;
                pairs[9] = valueFive;
                pairs[10] = keySix;
                pairs[11] = valueSix;
            }

            AsyncContextMap build() {
                if (nextIndex == 12) {
                    return new SixAsyncContextMap((Key<?>) pairs[0], pairs[1], (Key<?>) pairs[2], pairs[3],
                            (Key<?>) pairs[4], pairs[5], (Key<?>) pairs[6], pairs[7], (Key<?>) pairs[8], pairs[9],
                            (Key<?>) pairs[10], pairs[11]);
                }
                if (nextIndex == pairs.length) {
                    return new SevenOrMoreAsyncContextMap(pairs);
                }
                return new SevenOrMoreAsyncContextMap(Arrays.copyOf(pairs, nextIndex));
            }
        }
    }

    private static final class SevenOrMoreAsyncContextMap implements AsyncContextMap {
        /**
         * Array of <[i] = key, [i+1] = value> pairs.
         */
        private final Object[] context;

        private SevenOrMoreAsyncContextMap(Object... context) {
            this.context = context;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return context.length >>> 1;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get(Key<T> key) {
            int i = findIndex(key);
            return i < 0 ? null : (T) context[i + 1];
        }

        @Override
        public boolean contains(Key<?> key) {
            return findIndex(key) >= 0;
        }

        @Override
        public <T> AsyncContextMap put(Key<T> key, @Nullable T value) {
            int i = findIndex(Objects.requireNonNull(key));
            final Object[] context;
            if (i < 0) {
                context = new Object[this.context.length + 2];
                arraycopy(this.context, 0, context, 0, this.context.length);
                context[this.context.length] = key;
                context[this.context.length + 1] = value;
            } else {
                context = new Object[this.context.length];
                arraycopy(this.context, 0, context, 0, i + 1);
                context[i + 1] = value;
                if (i + 2 < context.length) {
                    arraycopy(this.context, i + 2, context, i + 2, context.length - i - 2);
                }
            }
            return new SevenOrMoreAsyncContextMap(context);
        }

        @Override
        public AsyncContextMap putAll(AsyncContextMap c) {
            if (c.isEmpty()) {
                return this;
            }
            PutIndexItemsIterator createKeyIndexItr = new PutIndexItemsIterator();
            // First pass is to see how many new entries there are to get the correct size of the new context array
            c.forEach(createKeyIndexItr);

            Object[] context = new Object[this.context.length + (createKeyIndexItr.newItems << 1)];
            arraycopy(this.context, 0, context, 0, this.context.length);

            // Second pass is to fill entries where the keys overlap, or append new entries to the end
            PutPopulateNewArrayIterator populateItr =
                    new PutPopulateNewArrayIterator(createKeyIndexItr.keyIndexes, this.context, context);
            c.forEach(populateItr);
            return new SevenOrMoreAsyncContextMap(context);
        }

        @Override
        public AsyncContextMap putAll(Map<Key<?>, Object> map) {
            PutAllConsumer consumer = new PutAllConsumer(map.size());
            // First pass is to see how many new entries there are to get the correct size of the new context array
            map.forEach(consumer);

            Object[] context = new Object[this.context.length + (consumer.newItems << 1)];
            arraycopy(this.context, 0, context, 0, this.context.length);

            // Second pass is to fill entries where the keys overlap, or append new entries to the end
            PutAllPopulateConsumer populateConsumer =
                    new PutAllPopulateConsumer(consumer.keyIndexes, this.context, context);
            map.forEach(populateConsumer);
            return new SevenOrMoreAsyncContextMap(context);
        }

        @Override
        public AsyncContextMap remove(Key<?> key) {
            int i = findIndex(key);
            if (i < 0) {
                return this;
            }
            if (size() == 7) {
                return removeBelowSeven(i);
            }
            Object[] context = new Object[this.context.length - 2];
            arraycopy(this.context, 0, context, 0, i);
            arraycopy(this.context, i + 2, context, i, this.context.length - i - 2);
            return new SevenOrMoreAsyncContextMap(context);
        }

        @Override
        public AsyncContextMap removeAll(AsyncContextMap c) {
            GrowableIntArray indexesToRemove = new GrowableIntArray(3);
            c.forEach((t, u) -> {
                int keyIndex = findIndex(t);
                if (keyIndex >= 0) {
                    indexesToRemove.add(keyIndex);
                }
                return Boolean.TRUE;
            });
            return removeAll0(indexesToRemove);
        }

        @Override
        public AsyncContextMap removeAll(Iterable<Key<?>> entries) {
            GrowableIntArray indexesToRemove = new GrowableIntArray(3);
            entries.forEach(key -> {
                int keyIndex = findIndex(key);
                if (keyIndex >= 0) {
                    indexesToRemove.add(keyIndex);
                }
            });

            return removeAll0(indexesToRemove);
        }

        @Override
        public AsyncContextMap clear() {
            return EMPTY_CONTEXT_MAP;
        }

        private int findIndex(Key<?> key) {
            for (int i = 0; i < context.length; i += 2) {
                if (key.equals(context[i])) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public Key<?> forEach(BiPredicate<Key<?>, Object> consumer) {
            for (int i = 0; i < context.length; i += 2) {
                final Key<?> key = (Key<?>) context[i];
                if (!consumer.test(key, context[i + 1])) {
                    return key;
                }
            }
            return null;
        }

        private AsyncContextMap removeAll0(GrowableIntArray indexesToRemove) {
            if (size() == indexesToRemove.size) {
                return EMPTY_CONTEXT_MAP;
            } else if (indexesToRemove.size == 0) {
                return this;
            } else if (size() - indexesToRemove.size < 7) {
                return removeBelowSeven(indexesToRemove);
            }

            Object[] context = new Object[this.context.length - (indexesToRemove.size << 1)];

            // Preserve entries that were not found in the first pass
            int newContextIndex = 0;
            for (int i = 0; i < this.context.length; i += 2) {
                if (indexesToRemove.isValueAbsent(i)) {
                    context[newContextIndex] = this.context[i];
                    context[newContextIndex + 1] = this.context[i + 1];
                    newContextIndex += 2;
                }
            }
            return new SevenOrMoreAsyncContextMap(context);
        }

        private AsyncContextMap removeBelowSeven(int i) {
            switch (i) {
                case 0:
                    return new SixAsyncContextMap((Key<?>) context[2], context[3],
                            (Key<?>) context[4], context[5],
                            (Key<?>) context[6], context[7],
                            (Key<?>) context[8], context[9],
                            (Key<?>) context[10], context[11],
                            (Key<?>) context[12], context[13]);
                case 2:
                    return new SixAsyncContextMap((Key<?>) context[0], context[1],
                            (Key<?>) context[4], context[5],
                            (Key<?>) context[6], context[7],
                            (Key<?>) context[8], context[9],
                            (Key<?>) context[10], context[11],
                            (Key<?>) context[12], context[13]);
                case 4:
                    return new SixAsyncContextMap((Key<?>) context[0], context[1],
                            (Key<?>) context[2], context[3],
                            (Key<?>) context[6], context[7],
                            (Key<?>) context[8], context[9],
                            (Key<?>) context[10], context[11],
                            (Key<?>) context[12], context[13]);
                case 6:
                    return new SixAsyncContextMap((Key<?>) context[0], context[1],
                            (Key<?>) context[2], context[3],
                            (Key<?>) context[4], context[5],
                            (Key<?>) context[8], context[9],
                            (Key<?>) context[10], context[11],
                            (Key<?>) context[12], context[13]);
                case 8:
                    return new SixAsyncContextMap((Key<?>) context[0], context[1],
                            (Key<?>) context[2], context[3],
                            (Key<?>) context[4], context[5],
                            (Key<?>) context[6], context[7],
                            (Key<?>) context[10], context[11],
                            (Key<?>) context[12], context[13]);
                case 10:
                    return new SixAsyncContextMap((Key<?>) context[0], context[1],
                            (Key<?>) context[2], context[3],
                            (Key<?>) context[4], context[5],
                            (Key<?>) context[6], context[7],
                            (Key<?>) context[8], context[9],
                            (Key<?>) context[12], context[13]);
                case 12:
                    return new SixAsyncContextMap((Key<?>) context[0], context[1],
                            (Key<?>) context[2], context[3],
                            (Key<?>) context[4], context[5],
                            (Key<?>) context[6], context[7],
                            (Key<?>) context[8], context[9],
                            (Key<?>) context[10], context[11]);
                default:
                    throw new RuntimeException("programming error. unable to remove i: " + i);
            }
        }

        private AsyncContextMap removeBelowSeven(GrowableIntArray indexesToRemove) {
            switch (size() - indexesToRemove.size) {
                case 1:
                    for (int i = 0; i < this.context.length; i += 2) {
                        if (indexesToRemove.isValueAbsent(i)) {
                            return new OneAsyncContextMap((Key<?>) context[i], context[i + 1]);
                        }
                    }
                    break;
                case 2: {
                    int keepI1 = -1;
                    for (int i = 0; i < this.context.length; i += 2) {
                        if (indexesToRemove.isValueAbsent(i)) {
                            if (keepI1 < 0) {
                                keepI1 = i;
                            } else {
                                return new TwoAsyncContextMap((Key<?>) context[keepI1], context[keepI1 + 1],
                                        (Key<?>) context[i], context[i + 1]);
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    int keepI1 = -1;
                    int keepI2 = -1;
                    for (int i = 0; i < this.context.length; i += 2) {
                        if (indexesToRemove.isValueAbsent(i)) {
                            if (keepI1 < 0) {
                                keepI1 = i;
                            } else if (keepI2 < 0) {
                                keepI2 = i;
                            } else {
                                return new ThreeAsyncContextMap((Key<?>) context[keepI1], context[keepI1 + 1],
                                        (Key<?>) context[keepI2], context[keepI2 + 1],
                                        (Key<?>) context[i], context[i + 1]);
                            }
                        }
                    }
                    break;
                }
                case 4: {
                    int keepI1 = -1;
                    int keepI2 = -1;
                    int keepI3 = -1;
                    for (int i = 0; i < this.context.length; i += 2) {
                        if (indexesToRemove.isValueAbsent(i)) {
                            if (keepI1 < 0) {
                                keepI1 = i;
                            } else if (keepI2 < 0) {
                                keepI2 = i;
                            } else if (keepI3 < 0) {
                                keepI3 = i;
                            } else {
                                return new FourAsyncContextMap((Key<?>) context[keepI1], context[keepI1 + 1],
                                        (Key<?>) context[keepI2], context[keepI2 + 1],
                                        (Key<?>) context[keepI3], context[keepI3 + 1],
                                        (Key<?>) context[i], context[i + 1]);
                            }
                        }
                    }
                    break;
                }
                case 5: {
                    int keepI1 = -1;
                    int keepI2 = -1;
                    int keepI3 = -1;
                    int keepI4 = -1;
                    for (int i = 0; i < this.context.length; i += 2) {
                        if (indexesToRemove.isValueAbsent(i)) {
                            if (keepI1 < 0) {
                                keepI1 = i;
                            } else if (keepI2 < 0) {
                                keepI2 = i;
                            } else if (keepI3 < 0) {
                                keepI3 = i;
                            } else if (keepI4 < 0) {
                                keepI4 = i;
                            } else {
                                return new FiveAsyncContextMap((Key<?>) context[keepI1], context[keepI1 + 1],
                                        (Key<?>) context[keepI2], context[keepI2 + 1],
                                        (Key<?>) context[keepI3], context[keepI3 + 1],
                                        (Key<?>) context[keepI4], context[keepI4 + 1],
                                        (Key<?>) context[i], context[i + 1]);
                            }
                        }
                    }
                    break;
                }
                case 6: {
                    int keepI1 = -1;
                    int keepI2 = -1;
                    int keepI3 = -1;
                    int keepI4 = -1;
                    int keepI5 = -1;
                    for (int i = 0; i < this.context.length; i += 2) {
                        if (indexesToRemove.isValueAbsent(i)) {
                            if (keepI1 < 0) {
                                keepI1 = i;
                            } else if (keepI2 < 0) {
                                keepI2 = i;
                            } else if (keepI3 < 0) {
                                keepI3 = i;
                            } else if (keepI4 < 0) {
                                keepI4 = i;
                            } else if (keepI5 < 0) {
                                keepI5 = i;
                            } else {
                                return new SixAsyncContextMap((Key<?>) context[keepI1], context[keepI1 + 1],
                                        (Key<?>) context[keepI2], context[keepI2 + 1],
                                        (Key<?>) context[keepI3], context[keepI3 + 1],
                                        (Key<?>) context[keepI4], context[keepI4 + 1],
                                        (Key<?>) context[keepI5], context[keepI5 + 1],
                                        (Key<?>) context[i], context[i + 1]);
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }
            throw new RuntimeException("programming error. unable to reduce: " + (size() - indexesToRemove.size));
        }

        private static final class GrowableIntArray {
            private int[] array;
            int size;

            GrowableIntArray(int size) {
                array = new int[size];
            }

            void add(int value) {
                if (size == array.length) {
                    int[] newArray = new int[array.length << 1];
                    arraycopy(array, 0, newArray, 0, array.length);
                    array = newArray;
                }
                array[size++] = value;
            }

            int get(int i) {
                return array[i];
            }

            boolean isValueAbsent(int value) {
                for (int i = 0; i < size; ++i) {
                    if (array[i] == value) {
                        return false;
                    }
                }
                return true;
            }
        }

        private final class PutIndexItemsIterator implements BiPredicate<Key<?>, Object> {
            int newItems;
            final GrowableIntArray keyIndexes = new GrowableIntArray(3);

            @Override
            public boolean test(Key<?> t, Object u) {
                int keyIndex = findIndex(t);
                if (keyIndex < 0) {
                    ++newItems;
                }
                keyIndexes.add(keyIndex);
                return Boolean.TRUE;
            }
        }

        private static final class PutPopulateNewArrayIterator implements BiPredicate<Key<?>, Object> {
            private int i;
            private int newItemIndex;
            private final GrowableIntArray keyIndexes;
            private final Object[] newContext;

            PutPopulateNewArrayIterator(GrowableIntArray keyIndexes, Object[] oldContext, Object[] newContext) {
                this.keyIndexes = keyIndexes;
                this.newContext = newContext;
                newItemIndex = oldContext.length;
            }

            @Override
            public boolean test(Key<?> t, Object u) {
                final int keyIndex = keyIndexes.get(i++);
                if (keyIndex < 0) {
                    newContext[newItemIndex] = t;
                    newContext[newItemIndex + 1] = u;
                    newItemIndex += 2;
                } else {
                    newContext[keyIndex] = t;
                    newContext[keyIndex + 1] = u;
                }
                return Boolean.TRUE;
            }
        }

        private final class PutAllConsumer implements BiConsumer<Key<?>, Object> {
            int newItems;
            final GrowableIntArray keyIndexes;

            PutAllConsumer(int mapSize) {
                keyIndexes = new GrowableIntArray(mapSize);
            }

            @Override
            public void accept(Key<?> key, Object o) {
                int keyIndex = findIndex(key);
                if (keyIndex < 0) {
                    ++newItems;
                }
                keyIndexes.add(keyIndex);
            }
        }

        private static final class PutAllPopulateConsumer implements BiConsumer<Key<?>, Object> {
            private int i;
            private int newItemIndex;
            private final GrowableIntArray keyIndexes;
            private final Object[] newContext;

            PutAllPopulateConsumer(GrowableIntArray keyIndexes, Object[] oldContext, Object[] newContext) {
                this.keyIndexes = keyIndexes;
                this.newContext = newContext;
                newItemIndex = oldContext.length;
            }

            @Override
            public void accept(Key<?> key, Object o) {
                final int keyIndex = keyIndexes.get(i++);
                if (keyIndex < 0) {
                    newContext[newItemIndex] = key;
                    newContext[newItemIndex + 1] = o;
                    newItemIndex += 2;
                } else {
                    newContext[keyIndex] = key;
                    newContext[keyIndex + 1] = o;
                }
            }
        }
    }

    abstract static class AbstractPutSevenBuilder implements BiPredicate<Key<?>, Object>, BiConsumer<Key<?>, Object> {
        Object[] pairs;
        int nextIndex;

        AbstractPutSevenBuilder(int arraySize, int nextIndex) {
            pairs = new Object[arraySize];
            this.nextIndex = nextIndex;
        }

        @Override
        public final void accept(final Key<?> key, final Object o) {
            test(key, o);
        }

        @Override
        public final boolean test(final Key<?> key, final Object o) {
            for (int i = 0; i < nextIndex; i += 2) {
                if (pairs[i].equals(key)) {
                    pairs[i + 1] = o;
                    return false;
                }
            }
            assert nextIndex <= pairs.length - 2;
            pairs[nextIndex] = key;
            pairs[nextIndex + 1] = o;
            nextIndex += 2;
            return true;
        }
    }

    private static final class MutableInt {
        int value;
    }
}
