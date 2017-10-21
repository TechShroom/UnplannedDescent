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
package com.techshroom.unplanned.ecs;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Component is made of fields stored in packed arrays.
 * 
 * Components define what fields they are made of, but they do NOT define the
 * storage order of these fields. This is handled by the association system, to
 * allow for optimal packing.
 */
public interface Component {

    static Map<String, ComponentField<?>> makeFieldMap(ComponentField<?>... fields) {
        return Stream.of(fields).collect(toImmutableMap(f -> f.getName(), Function.identity()));
    }

    UUID getId();

    Map<String, ComponentField<?>> getFields();

    /**
     * Unsafe accessor for field-by-name. This is used by the annotation
     * processor when the type of the field is known, and this cast is safe,
     * assuming no malicious runtime changes.
     * 
     * @param name
     *            - the name of the field to retrieve
     * @return the field
     */
    default <T> ComponentField<T> getField(String name) {
        @SuppressWarnings("unchecked")
        ComponentField<T> cast = (ComponentField<T>) getFields().get(name);
        return cast;
    }

}
