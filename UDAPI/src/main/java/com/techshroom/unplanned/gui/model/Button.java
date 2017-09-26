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

import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.font.FontDescriptor;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.event.keyboard.KeyState;
import com.techshroom.unplanned.event.keyboard.KeyStateEvent;
import com.techshroom.unplanned.event.mouse.MouseButtonEvent;
import com.techshroom.unplanned.geometry.CornerVector4i;
import com.techshroom.unplanned.geometry.SidedVector4i;
import com.techshroom.unplanned.gui.event.ActionEvent;
import com.techshroom.unplanned.gui.hooks.TextSizer;
import com.techshroom.unplanned.gui.model.constrhooks.HoverBackgroundColor;
import com.techshroom.unplanned.input.Key;

public class Button extends LabeledBase {
    
    {
        inherits(this, HoverBackgroundColor.INSTANCE);
    }

    private static final Border BUTTON_BORDER = Border.builder().radii(CornerVector4i.all(5)).build();
    private static final SidedVector4i BUTTON_PADDING = SidedVector4i.all(5);

    public static Builder<Button> builder() {
        return new Builder<>(Button::new);
    }

    protected Button(FontDescriptor font, String text, TextSizer textSizer) {
        super(font, text, textSizer);
        // setup styling to look like a button
        setBorder(BUTTON_BORDER);
        setBackgroundColor(Color.LIGHT_GRAY);
        setPadding(BUTTON_PADDING);
        setProperty(HoverBackgroundColor.PROP_HOVER_BACKGROUND_COLOR, Color.LIGHT_GRAY.lighter());
    }

    @Override
    protected Object getSubscriber() {
        return new Object() {

            @Subscribe
            public void onMouseClick(MouseButtonEvent event) {
                // trigger on release
                if (event.getButton() == 0 && !event.isDown()) {
                    getEventBus().post(ActionEvent.create());
                }
            }

            @Subscribe
            public void onKey(KeyStateEvent event) {
                // trigger on press
                if (event.is(Key.ENTER, KeyState.PRESSED)) {
                    getEventBus().post(ActionEvent.create());
                }
            }
        };
    }

}
