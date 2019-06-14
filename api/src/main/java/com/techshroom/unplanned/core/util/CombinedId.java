/*
 * This file is part of unplanned-descent, licensed under the MIT License (MIT).
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

import java.util.List;
import java.util.Locale;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@AutoValue
public abstract class CombinedId {

    private static List<String> splitUpperParts(String titleCase) {
        ImmutableList.Builder<String> l = ImmutableList.builder();
        StringBuilder nextPart = new StringBuilder();
        boolean nonUpper = false;
        for (int i = 0; i < titleCase.length(); i = titleCase.offsetByCodePoints(i, 1)) {
            int cp = titleCase.codePointAt(i);
            if (Character.isUpperCase(cp)) {
                if (nonUpper) {
                    // clear and append!
                    l.add(nextPart.toString());
                    nonUpper = false;
                    nextPart.setLength(0);
                }
            } else {
                nonUpper = true;
            }
            nextPart.append(Character.toChars(cp));
        }
        // append last
        l.add(nextPart.toString());
        return l.build();
    }

    public static CombinedId fromTitle(String titleCase) {
        String camel = UDStrings.lowercaseFirstLetter(titleCase);
        List<String> parts = splitUpperParts(titleCase);
        parts = Lists.transform(parts, s -> s.toLowerCase(Locale.ENGLISH));
        String dot = String.join(".", parts);
        String under = String.join("_", parts);
        return wrap(dot, under, camel, titleCase);
    }

    public static CombinedId wrap(String dot, String underscore, String camel, String title) {
        return new AutoValue_CombinedId(dot, underscore, camel, title);
    }

    CombinedId() {
    }

    public abstract String dotSeparated();

    public abstract String underscoreSeparated();

    public abstract String camelCase();

    public abstract String titleCase();

}
