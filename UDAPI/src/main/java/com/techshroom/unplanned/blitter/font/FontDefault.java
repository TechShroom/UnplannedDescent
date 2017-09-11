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
package com.techshroom.unplanned.blitter.font;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.techshroom.unplanned.blitter.GraphicsContext;

/**
 * Holds the default Font, loaded from internal UD resources. The family is
 * undefined, but this can be useful for debugging or a quick project.
 */
public class FontDefault {

    private static final String LOCATION = "com/techshroom/unplanned/fonts/noto";
    private static final String FAMILY = "NotoSans";
    private static final String PLAIN = "Regular";
    private static final String BOLD = "Bold";
    private static final String ITALIC = "Italic";
    private static final int DEFAULT_SIZE = 10;

    public static FontDescriptor getPlainDescriptor() {
        return getDescriptor(PLAIN);
    }

    public static FontDescriptor getBoldDescriptor() {
        return getDescriptor(BOLD);
    }

    public static FontDescriptor getItalicDescriptor() {
        return getDescriptor(ITALIC);
    }

    public static FontDescriptor getBoldItalicDescriptor() {
        return getDescriptor(BOLD + ITALIC);
    }

    private static FontDescriptor getDescriptor(String style) {
        String name = FAMILY + "-" + style;
        String ttf = LOCATION + "/" + name + ".ttf";
        return FontDescriptor.describe(name, ttf, DEFAULT_SIZE, FontDescriptor.Type.CLASSPATH);
    }

    public static Font loadPlain(GraphicsContext ctx) {
        return load(ctx, getPlainDescriptor());
    }

    public static Font loadBold(GraphicsContext ctx) {
        return load(ctx, getBoldDescriptor());
    }

    public static Font loadItalic(GraphicsContext ctx) {
        return load(ctx, getItalicDescriptor());
    }

    public static Font loadBoldItalic(GraphicsContext ctx) {
        return load(ctx, getBoldItalicDescriptor());
    }

    private static Font load(GraphicsContext ctx, FontDescriptor desc) {
        try {
            return ctx.getFontLoader().loadFont(desc);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
