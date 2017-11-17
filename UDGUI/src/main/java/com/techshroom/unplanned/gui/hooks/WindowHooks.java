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

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.techshroom.unplanned.gui.model.parent.RootElement;
import com.techshroom.unplanned.window.Window;

@AutoValue
public abstract class WindowHooks {

    public static WindowHooks forWindow(Window window) {
        return new AutoValue_WindowHooks(window);
    }

    abstract Window getWindow();

    @Memoized
    ImplWindowHooks getImpl() {
        return ImplWindowHooks.Factory.IMPLEMENTATION.forWindow(getWindow());
    }
    
    public final TextSizer getTextSizer() {
        return getImpl().getTextSizer();
    }

    /**
     * An element that can be used as the root of a GUI tree. This is useful for
     * creating percent-sized roots, as they will be sized to the window size.
     * 
     * <p>
     * This element will also automatically handle mouse and keyboard events.
     * It's really useful!
     * </p>
     * 
     * @return the window root element
     */
    @Memoized
    public RootElement getRootElement() {
        RootElement re = new RootElementImplementation();
        getWindow().getEventBus().register(re);
        return re;
    }

}
