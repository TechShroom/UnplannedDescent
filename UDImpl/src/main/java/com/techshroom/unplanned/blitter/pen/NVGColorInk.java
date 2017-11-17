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
package com.techshroom.unplanned.blitter.pen;

import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;

import org.lwjgl.nanovg.NVGColor;

import com.flowpowered.math.vector.Vector4f;
import com.techshroom.unplanned.core.util.Color;

public class NVGColorInk extends NVGInk {

    private static NVGColor allocateNvgColor(Color color) {
        NVGColor nvg = NVGColor.calloc();
        colorNvgColor(nvg, color);
        return nvg;
    }

    private static void colorNvgColor(NVGColor nvg, Color color) {
        Vector4f c = color.asVector4f();
        nvg.r(c.getX()).g(c.getY()).b(c.getZ()).a(c.getW());
    }

    private final NVGColor color;

    public NVGColorInk(Color color) {
        this.color = allocateNvgColor(color);
    }

    @Override
    public void applyFill(long ctx) {
        nvgFillColor(ctx, color);
    }

    @Override
    public void applyStroke(long ctx) {
        nvgStrokeColor(ctx, color);
    }

}
