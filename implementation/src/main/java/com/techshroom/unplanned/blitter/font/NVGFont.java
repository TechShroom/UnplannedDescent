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

import static org.lwjgl.nanovg.NanoVG.nvgFontFaceId;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.flowpowered.math.vector.Vector2f;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NVGFont implements Font {

    public static Builder builder() {
        return new AutoValue_NVGFont.Builder();
    }

    @AutoValue.Builder
    interface Builder {

        Builder fontData(ByteBuffer data);

        Builder nvgContext(long ctx);

        Builder nvgFontId(int id);

        Builder providedName(String name);

        Builder size(float size);

        Builder ascent(float ascent);

        Builder descent(float descent);

        Builder height(float height);

        NVGFont build();

    }

    // this is kinda violating the value class principle...
    private boolean deleted = false;

    NVGFont() {
    }

    abstract ByteBuffer getFontData();

    abstract long getNvgContext();

    abstract int getNvgFontId();

    public final void setAsCurrentFont() {
        nvgFontFaceId(getNvgContext(), getNvgFontId());
        nvgFontSize(getNvgContext(), getSize());
    }

    @Override
    public final Font withSize(float size) {
        return toBuilder().size(size).build();
    }

    @Override
    public final Vector2f getBoundingBox(String text) {
        setAsCurrentFont();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer bounds = stack.callocFloat(4);
            nvgTextBounds(getNvgContext(), 0, 0, text, bounds);
            return new Vector2f(bounds.get(2) - bounds.get(0), bounds.get(3) - bounds.get(1));
        }
    }

    abstract Builder toBuilder();

    @Override
    public void delete() {
        if (!deleted) {
            MemoryUtil.memFree(getFontData());
            deleted = true;
        }
    }

}
