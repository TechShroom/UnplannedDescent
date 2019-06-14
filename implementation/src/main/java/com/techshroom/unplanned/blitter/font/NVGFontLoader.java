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

package com.techshroom.unplanned.blitter.font;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
import static org.lwjgl.nanovg.NanoVG.nvgTextMetrics;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryStack;

import com.techshroom.unplanned.blitter.GLGraphicsContext;
import com.techshroom.unplanned.core.util.IOUtil;

public class NVGFontLoader implements FontLoader {

    private static int EXPECTED_FONT_SIZE = 1 * 1024 * 1024;
    private final GLGraphicsContext ctx;

    public NVGFontLoader(GLGraphicsContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Font loadFont(String name, InputStream ttf) throws IOException {
        long ctx = this.ctx.getNanoVgContext();
        NVGFont.Builder font = NVGFont.builder();

        ByteBuffer buf = IOUtil.ioResourceToByteBuffer(ttf, EXPECTED_FONT_SIZE);
        int id = nvgCreateFontMem(ctx, name, buf, 0);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer all = stack.callocFloat(3);
            FloatBuffer ascender = (FloatBuffer) all.duplicate().position(0);
            FloatBuffer descender = (FloatBuffer) all.duplicate().position(1);
            FloatBuffer lineh = (FloatBuffer) all.duplicate().position(2);
            nvgTextMetrics(ctx, ascender, descender, lineh);

            font.ascent(ascender.get()).descent(descender.get()).height(lineh.get());
        }
        font.fontData(buf);
        font.nvgFontId(id);
        font.providedName(name);
        font.nvgContext(ctx);
        font.size(1);
        return font.build();
    }

}
