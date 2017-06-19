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
package com.techshroom.unplanned.core;

import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSortedSet;

public final class Settings {

    private static <T> T load(String property, T defaultVal, Function<String, T> converter) {
        String p = System.getProperty(property);
        if (p == null) {
            return defaultVal;
        }
        return converter.apply(p);
    }

    private static String load(String property, String defaultVal) {
        return load(property, defaultVal, Function.identity());
    }

    private static final Set<String> TRUE = ImmutableSortedSet.orderedBy(String.CASE_INSENSITIVE_ORDER)
            .add("y", "yes", "true", "1")
            .build();

    private static boolean load(String property, boolean defaultVal) {
        return load(property, defaultVal, TRUE::contains);
    }

    public static final String APITRACE = load("ud.apitrace", "");
    public static final boolean FILL_TEXTURES_WITH_DEBUG_COLOR = load("ud.texture.debug.color", false);
    public static final boolean GRAPHICS_DEBUG = load("ud.graphics.debug", false);

    private Settings() {
    }

}
