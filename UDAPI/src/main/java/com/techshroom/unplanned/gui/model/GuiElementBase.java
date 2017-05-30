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
package com.techshroom.unplanned.gui.model;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector4i;

public class GuiElementBase implements GuiElement {

    private boolean visible;
    private Vector2i pos = Vector2i.ZERO;
    private Vector4i padding = Vector4i.ZERO;
    private Vector4i margin = Vector4i.ZERO;

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Vector2i getPosition() {
        return pos;
    }

    @Override
    public void setPosition(Vector2i pos) {
        this.pos = pos;
    }

    @Override
    public Vector4i getPadding() {
        return padding;
    }

    @Override
    public void setPadding(Vector4i padding) {
        this.padding = padding;
    }

    @Override
    public Vector4i getMargin() {
        return margin;
    }

    @Override
    public void setMargin(Vector4i margin) {
        this.margin = margin;
    }

}
