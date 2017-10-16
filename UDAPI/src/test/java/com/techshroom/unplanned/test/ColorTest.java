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

import static org.junit.Assert.*;

import java.util.function.Supplier;

import org.junit.Test;

import com.flowpowered.math.vector.Vector4f;
import com.flowpowered.math.vector.Vector4i;
import com.techshroom.unplanned.core.util.Color;

public class ColorTest {

    @Test
    public void testFromString() throws Exception {
        assertColorString(0xFF, 0xFF, 0xFF, 0xFF, "FFFFFF");
        assertColorString(0x12, 0x34, 0x56, 0xFF, "123456");
        assertColorString(0x12, 0x34, 0x56, 0x78, "12345678");
        assertColorString(0xFF, 0xFF, 0xFF, 0xFF, "FFF");
        assertColorString(0x11, 0x22, 0x33, 0xFF, "123");
        assertColorString(0x11, 0x22, 0x33, 0x44, "1234");
    }

    @Test
    public void testFromStringInvalid() throws Exception {
        assertCreateFail(() -> Color.fromString("FOO"));
        assertCreateFail(() -> Color.fromString("FOOF"));
        assertCreateFail(() -> Color.fromString("12345"));
    }

    @Test
    public void testOutOfBounds() throws Exception {
        assertCreateFail(() -> Color.fromFloat(5, 1, 1, 1));
        assertCreateFail(() -> Color.fromFloat(1, 5, 1, 1));
        assertCreateFail(() -> Color.fromFloat(1, 1, 5, 1));
        assertCreateFail(() -> Color.fromFloat(1, 1, 1, 5));
        assertCreateFail(() -> Color.fromFloat(-1, 1, 1, 1));
        assertCreateFail(() -> Color.fromFloat(1, -1, 1, 1));
        assertCreateFail(() -> Color.fromFloat(1, 1, -1, 1));
        assertCreateFail(() -> Color.fromFloat(1, 1, 1, -1));
    }

    @Test
    public void testConversions() throws Exception {
        assertColors(0xFF_FF_FF_FF, Color.WHITE);
        assertColors(0xFF_00_FF_00, Color.GREEN);
        assertColors(0xFF_FF_00_00, Color.BLUE);
        assertColors(0xFF_00_00_00, Color.BLACK);
        assertColors(0x78_56_34_12, Color.fromString("12345678"));
    }

    private void assertColors(int abgr, Color c) {
        int a = (abgr >> 24) & 0xFF;
        int b = (abgr >> 16) & 0xFF;
        int g = (abgr >> 8) & 0xFF;
        int r = (abgr >> 0) & 0xFF;
        assertEquals(abgr, c.asABGRInt());
        assertEquals(Vector4i.from(r, g, b, a), c.asVector4i());
        assertEquals(Vector4f.from(r, g, b, a).div(255), c.asVector4f());
    }

    @Test
    public void testFromFloat() throws Exception {
        assertColor(0, 0, 0, 0, Color.fromFloat(0, 0, 0, 0));
        assertColor(0xFF, 0xFF, 0xFF, 0xFF, Color.fromFloat(1, 1, 1, 1));
    }

    @Test
    public void testFromInt() throws Exception {
        assertColor(0, 0, 0, 0, Color.fromInt(0, 0, 0, 0));
        assertColor(0xFF, 0xFF, 0xFF, 0xFF, Color.fromInt(0xFF, 0xFF, 0xFF, 0xFF));
    }

    @Test
    public void testDarker() throws Exception {
        testDarkerHelper(Color.WHITE);
        testDarkerHelper(Color.BLUE);
        testDarkerHelper(Color.RED);
    }

    private void testDarkerHelper(Color color) {
        Color colorDarker = color.darker();
        boolean anyLower = false;
        int[] wArray = color.asVector4i().toArray();
        int[] wdArray = colorDarker.asVector4i().toArray();
        for (int i = 0; i < wArray.length; i++) {
            if (wdArray[i] < wArray[i]) {
                anyLower = true;
            } else if (wdArray[i] > wArray[i]) {
                fail("darker() raised values on some components.");
            }
        }
        assertTrue("darker() made no changes", anyLower);
    }

    @Test
    public void testLighter() throws Exception {
        testLigherHelper(Color.BLACK);
        testLigherHelper(Color.BLUE);
        testLigherHelper(Color.RED);
    }

    private void testLigherHelper(Color color) {
        Color colorLighter = color.lighter();
        boolean anyHigher = false;
        int[] bArray = color.asVector4i().toArray();
        int[] blArray = colorLighter.asVector4i().toArray();
        for (int i = 0; i < bArray.length; i++) {
            if (blArray[i] > bArray[i]) {
                anyHigher = true;
            } else if (blArray[i] < bArray[i]) {
                fail("lighter() lowered values on some components.");
            }
        }
        assertTrue("lighter() made no changes", anyHigher);
    }

    @Test
    public void testHslCycle() throws Exception {
        assertEquals(Color.BLACK, Color.fromHsl(Color.BLACK.toHsl()));
        assertEquals(Color.BLUE, Color.fromHsl(Color.BLUE.toHsl()));
        assertEquals(Color.RED, Color.fromHsl(Color.RED.toHsl()));
    }

    private void assertColorString(int r, int g, int b, int a, String color) {
        Color c = Color.fromString(color);
        assertColor(r, g, b, a, c);
        c = Color.fromString("#" + color);
        assertColor(r, g, b, a, c);
    }

    private void assertColor(int r, int g, int b, int a, Color c) {
        assertEquals(r, c.getRed());
        assertEquals(g, c.getGreen());
        assertEquals(b, c.getBlue());
        assertEquals(a, c.getAlpha());
    }

    private void assertCreateFail(Supplier<Color> create) {
        try {
            create.get();
            fail("Got a color.");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

}
