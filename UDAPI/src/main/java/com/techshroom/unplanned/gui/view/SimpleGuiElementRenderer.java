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
package com.techshroom.unplanned.gui.view;

import com.flowpowered.math.vector.Vector2i;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.pen.DigitalPen;
import com.techshroom.unplanned.gui.model.GuiElement;

/**
 * The simplest render. Simply fills the element's area with the background
 * color.
 * 
 * @param <E>
 *            the type of element, for extensions of this renderer
 */
public class SimpleGuiElementRenderer<E extends GuiElement> implements GuiElementRenderer<E> {

    @Override
    public void render(RenderJob<E> job) {
        E e = job.getElement();

        // shortcut if there's no color to draw
        if (e.getBackgroundColor().getAlpha() == 0) {
            return;
        }
        GraphicsContext ctx = job.getContext();
        DigitalPen pen = ctx.getPen();

        pen.draw(() -> {
            pen.setColor(e.getBackgroundColor());
            RenderUtility.applyStandardTransform(e, pen, false);
            Vector2i size = e.getSize().add(e.getPadding().getAsWidthHeight());
            pen.fill(() -> pen.rect(0, 0, size.getX(), size.getY()));
        });
    }

}
