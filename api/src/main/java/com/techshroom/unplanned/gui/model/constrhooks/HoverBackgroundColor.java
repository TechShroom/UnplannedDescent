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

package com.techshroom.unplanned.gui.model.constrhooks;

import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.gui.event.HoverEndEvent;
import com.techshroom.unplanned.gui.event.HoverStartEvent;
import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.PropertyKey;

public enum HoverBackgroundColor implements ConstrHook<GuiElement> {
    INSTANCE;

    public static final PropertyKey<Color> PROP_HOVER_BACKGROUND_COLOR = PropertyKey.of("hoverBackgroundColor");

    private static final class HCElementHook {

        private static final PropertyKey<Color> PROP_PREV_BG_COLOR = PropertyKey.unique("prevBgColor");
        private final GuiElement e;

        protected HCElementHook(GuiElement e) {
            this.e = e;
        }

        @Subscribe
        public void onHoverStart(HoverStartEvent event) {
            Color hoverBgColor = e.getProperty(PROP_HOVER_BACKGROUND_COLOR);
            if (hoverBgColor == null) {
                return;
            }
            e.setProperty(PROP_PREV_BG_COLOR, e.getBackgroundColor());
            e.setBackgroundColor(hoverBgColor);
        }

        @Subscribe
        public void onHoverEnd(HoverEndEvent event) {
            Color prevBgColor = e.getProperty(PROP_PREV_BG_COLOR);
            if (prevBgColor == null) {
                return;
            }
            e.removeProperty(PROP_PREV_BG_COLOR);
            e.setBackgroundColor(prevBgColor);
        }
    }

    @Override
    public void onConstruction(GuiElement e) {
        e.getEventBus().register(new HCElementHook(e));
    }

}
