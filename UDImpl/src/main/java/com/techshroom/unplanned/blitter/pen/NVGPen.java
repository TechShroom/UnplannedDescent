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

import com.techshroom.unplanned.blitter.GLGraphicsContext;
import com.techshroom.unplanned.blitter.font.Font;
import com.techshroom.unplanned.blitter.font.FontDefault;
import com.techshroom.unplanned.core.util.Color;

public class NVGPen implements DigitalPen {

    private final GLGraphicsContext graphics;
    private Color color = Color.BLACK;
    private Font font;

    public NVGPen(GLGraphicsContext graphics) {
        this.graphics = graphics;
    }

    public void initialize() {
        this.font = FontDefault.loadPlain(graphics);
    }

    public void destroy() {
        this.font.delete();
        this.font = null;
    }

    private long ctx() {
        return this.graphics.getNanoVgContext();
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }

    @Override
    public Font getFont() {
        return font;
    }

}
