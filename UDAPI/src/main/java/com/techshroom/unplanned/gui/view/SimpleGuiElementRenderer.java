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

import java.util.List;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.Shape;
import com.techshroom.unplanned.blitter.binding.Bindable;
import com.techshroom.unplanned.blitter.transform.TransformStack;
import com.techshroom.unplanned.geometry.Plane;
import com.techshroom.unplanned.gui.model.GuiElement;

/**
 * The simplest render. Simply fills the element's area with the background
 * color.
 * 
 * @param <E>
 *            the type of element, for extensions of this renderer
 */
public class SimpleGuiElementRenderer<E extends GuiElement> implements GuiElementRenderer<E> {

    private static final String FILL_SHAPE = RenderUtility.key(SimpleGuiElementRenderer.class, "FILL_SHAPE");
    private static final List<Vector2d> FULL_TEXTURE = ImmutableList.of(
            new Vector2d(0, 0),
            new Vector2d(0, 1),
            new Vector2d(1, 0),
            new Vector2d(1, 1));

    @Override
    public void render(RenderJob<E> job) {
        E e = job.getElement();
        GraphicsContext ctx = job.getContext();
        Shape fillShape = getShape(job);
        try (TransformStack stack = ctx.pushTransformer();
                Bindable color = RenderUtility.getColorTexture(ctx, e.getBackgroundColor()).bind()) {
            stack.model().translate(e.getRelativePosition().toVector3().toFloat());
            stack.apply(ctx.getMatrixUploader());

            fillShape.draw();
        }
    }

    private Shape getShape(RenderJob<E> job) {
        return (Shape) job.getRenderCache()
                .computeIfAbsent(FILL_SHAPE, k -> initializeShape(job.getContext(), job.getElement()));
    }

    private Shape initializeShape(GraphicsContext ctx, E element) {
        Shape shape = ctx.getShapes().quad().shape(Plane.XY, Vector2i.ZERO, element.getSize(), FULL_TEXTURE);
        shape.initialize();
        return shape;
    }

}
