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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * An abstract class to infer {@link ParameterizedType}s for a child class.
 * This type can be retrieved from {@link #getType()}.
 * <p>
 * In order to use this, one has to create an anonymous sub-class, like:
 *
 * <pre>
 *  TypeHolder&lt;Set&lt;String&gt;&gt; holder = new TypeHolder&lt;Set&lt;String&gt;&gt;() { };
 * </pre>
 *
 * This implementation is based on the samples provided in
 * <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html">this article.</a>.
 *
 * @param <T> Type to be inferred.
 */
public abstract class TypeHolder<T> {

    private final Type type;

    /**
     * Creates a new instance.
     */
    protected TypeHolder() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException(superclass + " is not a parameterized type.");
        }
    }

    /**
     * Returns the inferred generic type for this {@link TypeHolder}.
     *
     * @return Inferred generic type for this {@link TypeHolder}.
     */
    public final Type getType() {
        return type;
    }
}
