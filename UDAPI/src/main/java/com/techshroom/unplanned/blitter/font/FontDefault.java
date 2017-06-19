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

    public static Font loadPlain(GraphicsContext ctx) {
        return load(ctx, PLAIN);
    }

    public static Font loadBold(GraphicsContext ctx) {
        return load(ctx, BOLD);
    }

    public static Font loadItalic(GraphicsContext ctx) {
        return load(ctx, ITALIC);
    }

    public static Font loadBoldItalic(GraphicsContext ctx) {
        return load(ctx, BOLD + ITALIC);
    }

    private static Font load(GraphicsContext ctx, String style) {
        String name = FAMILY + "-" + style;
        String ttf = LOCATION + name + ".ttf";
        try {
            return ctx.getFontLoader().loadFontFromClasspath(name, ttf);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
