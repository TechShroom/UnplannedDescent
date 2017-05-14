/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshoom.com>
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
package com.techshroom.unplanned.blitter.binding;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.blitter.Drawable;

/**
 * Handles binding and unbinding multiple {@link Bindable Bindables} and drawing
 * multiple {@link Drawable Drawables}. Binds in the order of the list, unbinds
 * in reverse order.
 */
public final class BindableDrawableSequence extends BindableSequenceBase implements BindableDrawable {

    public static BindableDrawableSequence of(Bindable... bindables) {
        return new BindableDrawableSequence(ImmutableList.copyOf(bindables), ImmutableList.of());
    }

    public static BindableDrawableSequence of(List<Bindable> bindables, List<Drawable> drawables) {
        return new BindableDrawableSequence(ImmutableList.copyOf(bindables), ImmutableList.copyOf(drawables));
    }

    private final List<Drawable> drawableSequence;

    private BindableDrawableSequence(ImmutableList<Bindable> bindSeq, ImmutableList<Drawable> drawSeq) {
        super(bindSeq);
        this.drawableSequence = drawSeq;
    }

    @Override
    public void drawWithoutBinding() {
        drawableSequence.forEach(Drawable::draw);
    }

}
