/*
 * (C) Copyright 2016 Red Hat and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.vorburger.equalshelper;

import java.util.function.BiFunction;

/**
 * Utility helping to implement readable equals() methods.
 *
 * @author Michael Vorburger, Red Hat
 */
public final class MoreObjects2 {

    @SuppressWarnings("unchecked")
    public static <T> boolean equalsHelper(T this_, Object obj, BooleanEqualsFunction<T> equals) {
        // Arguments intentionally called this_ & obj so that on syntax completion
        // so you can just remove "_" and it's "this" and "obj" matches java.lang.Object.equals(Object)
        if (obj == null) {
            return false;
        }
        if (obj == this_) {
            return true;
        }
        if (this_.getClass() != obj.getClass()) {
            return false;
        }
        return equals.apply(this_, (T) obj);
    }

    @FunctionalInterface
    public static interface BooleanEqualsFunction<T> extends BiFunction<T, T, Boolean> { }

    private MoreObjects2() { }
}
