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

import java.util.Set;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Utilities for user experience.
 */
public final class UX {

    private static final Set<String> TRUE = ImmutableSortedSet.orderedBy(String.CASE_INSENSITIVE_ORDER)
            .add("y", "yes", "true", "1")
            .build();

    /**
     * Determine if the user input resembles a positive / true value.
     * 
     * <p>
     * Examples:
     * <ul>
     * <li>y</li>
     * <li>yes</li>
     * <li>true</li>
     * <li>1</li>
     * </ul>
     * </p>
     * 
     * @param userInput
     *            - input from the user
     * @return {@code true} if it resembles a truthy value
     */
    public static boolean userInputIsTrue(String userInput) {
        return TRUE.contains(userInput);
    }

    private static final Set<String> FALSE = ImmutableSortedSet.orderedBy(String.CASE_INSENSITIVE_ORDER)
            .add("n", "no", "false", "0")
            .build();

    /**
     * Determine if the user input resembles a negative / false value.
     * 
     * <p>
     * Examples:
     * <ul>
     * <li>n</li>
     * <li>no</li>
     * <li>false</li>
     * <li>0</li>
     * </ul>
     * </p>
     * 
     * @param userInput
     *            - input from the user
     * @return {@code true} if it resembles a falsy value
     */
    public static boolean userInputIsFalse(String userInput) {
        return FALSE.contains(userInput);
    }

}
