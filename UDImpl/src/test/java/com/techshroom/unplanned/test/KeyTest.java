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
package com.techshroom.unplanned.test;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.lwjgl.glfw.GLFW;

import com.techshroom.unplanned.input.Key;
import com.techshroom.unplanned.input.KeyModifier;

/**
 * Testing for the {@link Key} class.
 */
public class KeyTest {

    /**
     * Ensure that the translation works backwards from
     * {@code Key -> GLFW constant}.
     */
    @Test
    public void ensureKeyNamesExist() {
        List<String> keyNames = grabKeyConstantNames(Key.values(), "KEY");
        for (String constant : keyNames) {
            Object constVal = null;
            try {
                constVal = GLFW.class.getDeclaredField(constant);
            } catch (NoSuchFieldException | SecurityException e) {
                // rip.
            }
            assertNotNull("no such key constant " + constant, constVal);
        }
    }

    /**
     * Ensure that the translation works backwards from
     * {@code KeyModifer -> GLFW constant}.
     */
    @Test
    public void ensureKeyModifierNamesExist() {
        List<String> keyNames = grabKeyConstantNames(KeyModifier.values(), "MOD");
        for (String constant : keyNames) {
            Object constVal = null;
            try {
                constVal = GLFW.class.getDeclaredField(constant);
            } catch (NoSuchFieldException | SecurityException e) {
                // rip.
            }
            assertNotNull("no such mod constant " + constant, constVal);
        }
    }

    private <E extends Enum<E>> List<String> grabKeyConstantNames(E[] values, String type) {
        return Stream.of(values)
                .map(k -> "GLFW_" + type + "_" + k.name().replaceFirst("NUM_(\\d)", "$1"))
                .collect(toImmutableList());
    }
}
