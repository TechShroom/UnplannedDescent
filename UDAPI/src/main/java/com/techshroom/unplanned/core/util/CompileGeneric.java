/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshroom.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.techshroom.unplanned.core.util;

/**
 * A class for forcing generics on methods.
 */
public class CompileGeneric<T> {

    private static final CompileGeneric<?> NON_CLASS_INSTANCE =
            new CompileGeneric<>();

    public static final class ClassCompileGeneric<T> extends CompileGeneric<T> {

        private final Class<?> rawClass;

        private ClassCompileGeneric(Class<?> rawClass) {
            this.rawClass = rawClass;
        }

        public Class<?> getRawClass() {
            return this.rawClass;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((this.rawClass == null) ? 0 : this.rawClass.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ClassCompileGeneric<?> other = (ClassCompileGeneric<?>) obj;
            if (this.rawClass == null) {
                if (other.rawClass != null)
                    return false;
            } else if (!this.rawClass.equals(other.rawClass))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return super.toString() + "[rawClass=" + this.rawClass.getName()
                    + "]";
        }

    }

    public static <T> ClassCompileGeneric<T> specify(Class<?> rawClass) {
        return new ClassCompileGeneric<>(rawClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> CompileGeneric<T> specify() {
        return (CompileGeneric<T>) NON_CLASS_INSTANCE;
    }

    private CompileGeneric() {
    }

    @Override
    public String toString() {
        return getClass().getName().replaceFirst("(?:.+\\.)*(.+?)$", "$1");
    }

}
