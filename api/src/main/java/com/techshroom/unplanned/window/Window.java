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

package com.techshroom.unplanned.window;

import com.flowpowered.math.vector.Vector2i;
import com.google.common.eventbus.EventBus;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.gui.model.parent.RootElement;
import com.techshroom.unplanned.input.Keyboard;
import com.techshroom.unplanned.input.Mouse;

/**
 * Window API.
 */
public interface Window {

    EventBus getEventBus();

    Vector2i getSize();

    Vector2i getFramebufferSize();

    Vector2i getLocation();

    Mouse getMouse();

    Keyboard getKeyboard();

    String getTitle();

    Monitor getMonitor();

    String getClipboardContents();

    boolean isVsyncOn();

    boolean isCloseRequested();

    boolean isVisible();

    /**
     * Gets an attribute value.
     * 
     * @param attr
     *            - The attribute to get
     * @return The attribute value
     * @deprecated Use only for unsupported attribute access
     */
    @Deprecated
    int getAttribute(int attr);

    /**
     * Please be careful with this window pointer. Certain properties are only
     * kept up-to-date via the provided setters in this class and will become
     * out-of-date if the values are changed elsewhere.
     * 
     * <p>
     * It is recommended to only use this pointer for unsupported features.
     * </p>
     * 
     * @return the window pointer
     */
    long getWindowPointer();

    void setSize(Vector2i size);

    void setVsyncOn(boolean on);

    void setCloseRequested(boolean requested);

    void setMinimized(boolean minimized);

    void setVisible(boolean visible);

    void setTitle(String title);

    GraphicsContext getGraphicsContext();

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
    RootElement getRootElement();

    void destroy();

    void processEvents();

    void waitForEvents();

}
