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

import java.util.Iterator;
import java.util.Objects;

import com.google.common.collect.Iterators;

public final class UDStrings {

    /**
     * Returns the amount of <tt>c</tt>'s in <tt>s</tt>
     * 
     * @param s
     *            - the string to check
     * @param c
     *            - the char to count
     * @return the number of <tt>c</tt>'s in <tt>s</tt>
     */
    public static int count(String s, char c) {
        // int cast is okay because string is never longer than int
        return (int) s.chars().filter(sc -> sc == c).count();
    }

    /**
     * Returns the matching <tt>char</tt> for <tt>number</tt>. Only works with
     * numbers 0-9.
     * 
     * @param number
     *            - a number from 0-9
     * @return the char that matches
     */
    public static char getCharForNum(byte number) {
        if (number > 9) {
            throw new IndexOutOfBoundsException("number > 9");
        }
        if (number < 0) {
            throw new IndexOutOfBoundsException("number < 0");
        }
        return (char) (number + '0');
    }

    /**
     * Returns the matching <tt>number</tt> for <tt>c</tt>. Only works with
     * chars '0'-'9'.
     * 
     * @param c
     *            - a char from '0'-'9'
     * @return the char that matches
     */
    public static byte getNumForChar(char c) {
        if (c > '9') {
            throw new IndexOutOfBoundsException("c > '9'");
        }
        if (c < '0') {
            throw new IndexOutOfBoundsException("c < '0'");
        }
        return (byte) (c - '0');
    }

    public static <X> String prettyJoinAnd(Iterable<X> items) {
        return prettyJoin(",", "and", items);
    }

    public static String prettyJoinAnd(Object... items) {
        return prettyJoinAnd(() -> Iterators.forArray(items));
    }

    public static <X> String prettyJoinOr(Iterable<X> items) {
        return prettyJoin(",", "or", items);
    }

    public static String prettyJoinOr(Object... items) {
        return prettyJoinOr(() -> Iterators.forArray(items));
    }

    public static String prettyJoin(String join, String lastPrefix,
            Object... items) {
        return prettyJoin(join, lastPrefix, () -> Iterators.forArray(items));
    }

    /**
     * Joins {@code items} by {@code join} if there is more than two, prefixing
     * the last item with {@code " " + lastPrefix} if there is more than one,
     * and prefixing every item but the first with {@code " "}.
     * <p>
     * This is useful for generating lists that are viewed by users by setting
     * {@code lastPrefix} to "and" or "or":
     * 
     * <pre>
     * Iterable<String> items = Stream.of("item1")::iterator;
     * Strings.prettyJoin(",", "and", items) -> "item1"
     * items = Stream.of("item1", "item2")::iterator;
     * Strings.prettyJoin(",", "and", items) -> "item1 and item2"
     * items = Stream.of("item1", "item2", "item3")::iterator;
     * Strings.prettyJoin(",", "and", items) -> "item1, item2, and item3"
     * </pre>
     * 
     * These 2 cases are common enough for overrides to exist,
     * {@link #prettyJoinAnd} and {@link #prettyJoinOr}.
     * </p>
     * 
     * @param join
     * @param lastPrefix
     * @param items
     * @return the joined strings
     */
    public static <X> String prettyJoin(String join, String lastPrefix,
            Iterable<X> items) {
        Iterator<? extends CharSequence> iter =
                Iterators.transform(items.iterator(), Objects::toString);
        if (!iter.hasNext()) {
            // no items quick-return
            return "";
        }
        // start with item1
        StringBuilder b = new StringBuilder(iter.next());
        if (iter.hasNext()) {
            iter = Iterators.transform(iter, e -> " " + e);
            CharSequence item2 = iter.next();
            if (iter.hasNext()) {
                // 3+ items mode, use join
                b.append(join).append(item2);
                while (iter.hasNext()) {
                    CharSequence next = iter.next();
                    b.append(join);
                    if (!iter.hasNext()) {
                        b.append(" ").append(lastPrefix);
                    }
                    b.append(next);
                }
            } else {
                // 2+ items mode, use lastPrefix
                b.append(" ").append(lastPrefix).append(item2);
            }
        }
        return b.toString();
    }

    public static String lowercaseFirstLetter(String input) {
        int afterFirstCp = input.offsetByCodePoints(0, 1);
        int firstLowerCp = Character.toLowerCase(input.codePointAt(0));
        return String.valueOf(Character.toChars(firstLowerCp)) + input.substring(afterFirstCp);
    }

    public static String uppercaseFirstLetter(String input) {
        int afterFirstCp = input.offsetByCodePoints(0, 1);
        int firstUpperCp = Character.toUpperCase(input.codePointAt(0));
        return String.valueOf(Character.toChars(firstUpperCp)) + input.substring(afterFirstCp);
    }

}
