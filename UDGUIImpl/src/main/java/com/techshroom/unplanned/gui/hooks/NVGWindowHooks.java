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
package com.techshroom.unplanned.gui.hooks;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.auto.service.AutoService;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.techshroom.unplanned.window.GLFWWindow;
import com.techshroom.unplanned.window.Window;

@AutoValue
public abstract class NVGWindowHooks implements ImplWindowHooks {
    
    @AutoService(ImplWindowHooks.Factory.class)
    public static final class Factory implements ImplWindowHooks.Factory {

        @Override
        public ImplWindowHooks forWindow(Window window) {
            checkArgument(window instanceof GLFWWindow, "unexpected window class %s", window.getClass());
            return new AutoValue_NVGWindowHooks((GLFWWindow) window);
        }
        
    }
    
    NVGWindowHooks() {
    }
    
    abstract GLFWWindow getWindow();

    @Override
    @Memoized
    public TextSizer getTextSizer() {
        return new NVGTextSizer(getWindow().getGraphicsContext());
    }

}
