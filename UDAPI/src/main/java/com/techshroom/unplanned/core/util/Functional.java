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

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.techshroom.unplanned.core.util.CompileGeneric.ClassCompileGeneric;

public final class Functional {

    public interface ThrowingFunction<I, R> {

        R apply(I t) throws Exception;

    }

    public static <I, R> Function<I, R>
            printErrors(ThrowingFunction<I, R> original) {
        return printErrors(original,
                CompileGeneric.<Function<I, R>> specify(Function.class),
                CompileGeneric.<ThrowingFunction<I, R>> specify(
                        ThrowingFunction.class));
    }

    public static <I, R> Function<I, R> printErrors(
            ThrowingFunction<I, R> original, Supplier<?> returnOnFail) {
        return printErrors(original,
                CompileGeneric.<Function<I, R>> specify(Function.class),
                CompileGeneric.<ThrowingFunction<I, R>> specify(
                        ThrowingFunction.class),
                returnOnFail);
    }

    public static <T> T printErrors(T original,
            ClassCompileGeneric<T> functionalInterface) {
        return printErrors(original, functionalInterface, functionalInterface,
                () -> null);
    }

    public static <E, T> E printErrors(T original,
            ClassCompileGeneric<E> resultInterface,
            ClassCompileGeneric<T> originalInterface) {
        return printErrors(original, resultInterface, originalInterface,
                () -> null);
    }

    @SuppressWarnings("unchecked")
    public static <E, T> E printErrors(T original,
            ClassCompileGeneric<E> resultInterface,
            ClassCompileGeneric<T> originalInterface,
            Supplier<?> returnOnFail) {
        if (originalInterface.getRawClass()
                .getDeclaredAnnotation(FunctionalInterface.class) == null) {
            verifyFunctional(originalInterface.getRawClass());
        }
        // TODO: optimize
        Method functionCall =
                locateFunctionalMethod(originalInterface.getRawClass());
        return (E) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] { resultInterface.getRawClass() },
                (proxy, method, args) -> {
                    try {
                        return functionCall.invoke(original, args);
                    } catch (Exception e) {
                        Throwable unwrapped = e;
                        if (unwrapped instanceof InvocationTargetException) {
                            unwrapped = unwrapped.getCause();
                        }
                        unwrapped.printStackTrace();
                        return returnOnFail.get();
                    }
                });
    }

    /**
     * Verify that the given class could be a functional interface.
     * 
     * @param clazz
     */
    private static void verifyFunctional(Class<?> clazz) {
        checkArgument(clazz.isInterface(), "not interface");
        checkArgument(Stream.of(clazz.getMethods())
                .filter(m -> Modifier.isAbstract(m.getModifiers()))
                .count() == 1, "more than one abstract method");
    }

    private static Method locateFunctionalMethod(Class<?> clazz) {
        return Stream.of(clazz.getMethods())
                .filter(m -> Modifier.isAbstract(m.getModifiers())).findFirst()
                .get();
    }

    private Functional() {
    }
}
