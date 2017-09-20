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
package com.techshroom.unplanned.gui.model.layout;

import com.flowpowered.math.vector.Vector2i;

public class VBoxLayout extends XBoxLayout {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private double spacing = 0;
        private boolean fillWidth = true;
        private Alignment alignment = Alignment.CENTER;

        private Builder() {
        }

        public Builder spacing(double spacing) {
            this.spacing = spacing;
            return this;
        }

        public Builder fillWidth(boolean fillWidth) {
            this.fillWidth = fillWidth;
            return this;
        }

        public Builder alignment(Alignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public VBoxLayout build() {
            return new VBoxLayout(spacing, fillWidth, alignment);
        }

    }

    private VBoxLayout(double spacing, boolean fillWidth, Alignment alignment) {
        super("Vgrow", spacing, fillWidth, alignment);
    }

    @Override
    protected int extractComponent(Vector2i vec) {
        return vec.getY();
    }

    @Override
    protected Vector2i setComponent(Vector2i vec, int compValue) {
        return Vector2i.from(vec.getX(), compValue);
    }

    @Override
    protected int extractComponentCross(Vector2i vec) {
        return vec.getX();
    }

    @Override
    protected Vector2i setComponentCross(Vector2i vec, int compValue) {
        return Vector2i.from(compValue, vec.getY());
    }

}
