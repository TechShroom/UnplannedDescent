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

import java.util.function.Consumer;

import com.flowpowered.math.vector.Vector2d;
import com.techshroom.unplanned.blitter.font.Font;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.geometry.CornerVector4i;
import com.techshroom.unplanned.gui.hooks.TextSizer;

public interface DigitalPen extends TextSizer {

    /**
     * Start a frame for the pen. Must be called before any draw calls.
     */
    void uncap();

    /**
     * End a frame for the pen. Must be called after all draw calls (for the
     * frame).
     */
    void cap();

    void save();

    void restore();

    default void draw(Runnable stuff) {
        draw($ -> stuff.run());
    }

    default void draw(Consumer<DigitalPen> stuff) {
        save();
        try {
            stuff.accept(this);
        } finally {
            restore();
        }
    }

    void scale(Vector2d scale);

    void translate(Vector2d vec);

    void setColor(Color color);

    Color getColor();

    void setFont(Font font);

    Font getFont();

    void begin();

    void fill();

    default void fill(Runnable path) {
        begin();
        path.run();
        fill();
    }

    void stroke();

    default void stroke(Runnable path) {
        begin();
        path.run();
        stroke();
    }

    void rect(float x, float y, float w, float h);

    void roundedRect(float x, float y, float w, float h, float r);

    void roundedRectVarying(float x, float y, float w, float h, float topLeft, float topRight, float bottomRight, float bottomLeft);

    default void roundedRectVarying(float x, float y, float w, float h, CornerVector4i r) {
        roundedRectVarying(x, y, w, h, r.getX(), r.getY(), r.getZ(), r.getW());
    }

    void textAlignment(TextAlignmentH alignHorizontal, TextAlignmentV alignVertical);

    void fillText(int x, int y, String text);

}
