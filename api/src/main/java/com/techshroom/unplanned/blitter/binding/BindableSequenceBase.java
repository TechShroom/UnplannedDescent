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

package com.techshroom.unplanned.blitter.binding;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;

class BindableSequenceBase implements Bindable {

    protected final ImmutableList<Bindable> sequence;
    protected final ImmutableList<Bindable> sequenceReversed;
    private boolean bound;

    protected BindableSequenceBase(ImmutableList<Bindable> sequence) {
        this.sequence = sequence;
        this.sequenceReversed = sequence.reverse();
    }

    @Override
    public BindableSequenceBase bind() {
        checkState(!bound, "already bound");
        sequence.forEach(Bindable::bind);
        bound = true;
        return this;
    }

    @Override
    public void unbind() {
        checkState(bound, "not bound");
        sequenceReversed.forEach(Bindable::unbind);
        bound = false;
    }

}