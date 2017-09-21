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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import com.flowpowered.math.vector.Vector2d;
import com.techshroom.unplanned.blitter.font.FontDefault;
import com.techshroom.unplanned.blitter.font.FontDescriptor;
import com.techshroom.unplanned.gui.hooks.TextSizer;

public class LabeledBase extends GuiElementBase implements Labeled {

    protected interface LabelConstructor<L extends LabeledBase> {

        L construct(FontDescriptor font, String text, TextSizer textSizer);
    }

    public static final class Builder<L extends LabeledBase> {

        private final LabelConstructor<L> constructor;
        private FontDescriptor font = FontDefault.getPlainDescriptor();
        private String text = "";
        @Nullable
        private TextSizer textSizer;

        protected Builder(LabelConstructor<L> constructor) {
            this.constructor = constructor;
        }

        public Builder<L> font(FontDescriptor font) {
            this.font = font;
            return this;
        }

        public Builder<L> text(String text) {
            this.text = text;
            return this;
        }

        public Builder<L> textSizer(TextSizer textSizer) {
            this.textSizer = textSizer;
            return this;
        }

        public L build() {
            return constructor.construct(font, text, textSizer);
        }

    }

    private FontDescriptor font = FontDefault.getPlainDescriptor();
    private String text;
    @Nullable
    private TextSizer textSizer;

    protected LabeledBase(FontDescriptor font, String text, @Nullable TextSizer textSizer) {
        this.font = font;
        this.text = text;
        this.textSizer = textSizer;
    }

    @Override
    protected void onRevalidation() {
        if (textSizer != null) {
            // bind size to textSizer
            Vector2d textSize = textSizer.sizeText(text, font);
            // Vector2d actualSize =
            // textSize.add(getPadding().getAsWidthHeight().toDouble());
            setPreferredSize(textSize.toInt());
            // this will be transferred to size in super, if it's not set...
        }
        super.onRevalidation();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        checkNotNull(text);
        this.text = text;
        invalidate();
    }

    @Override
    public FontDescriptor getFont() {
        return font;
    }

    @Override
    public void setFont(FontDescriptor font) {
        checkNotNull(font);
        this.font = font;
        invalidate();
    }

    @Override
    public TextSizer getTextSizer() {
        return textSizer;
    }

    @Override
    public void setTextSizer(TextSizer textSizer) {
        this.textSizer = textSizer;
        invalidate();
    }

}
