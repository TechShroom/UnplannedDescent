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

import com.techshroom.unplanned.blitter.pen.TextAlignmentH;
import com.techshroom.unplanned.blitter.pen.TextAlignmentV;
import com.techshroom.unplanned.gui.model.Labeled;

public class LabeledRenderer implements GuiElementRenderer<Labeled> {

    private final SimpleGuiElementRenderer<Labeled> bgDrawer = new SimpleGuiElementRenderer<>();

    @Override
    public void render(RenderJob<Labeled> job) {
        bgDrawer.render(job);
        Labeled e = job.getElement();
        job.getContext().getPen().draw(pen -> {
            pen.setColor(e.getForegroundColor());
            pen.setFont(job.getContext().getFontCache().getOrLoadFont(e.getFont()));
            RenderUtility.applyStandardTransform(e, pen, true);

            pen.textAlignment(TextAlignmentH.LEFT, TextAlignmentV.TOP);

            pen.fillText(0, 0, e.getText());
        });
    }

}
