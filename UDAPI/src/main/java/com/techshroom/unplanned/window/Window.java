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
package com.techshroom.unplanned.window;

import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.input.Keyboard;
import com.techshroom.unplanned.input.Mouse;
import com.techshroom.unplanned.monitor.Monitor;
import com.techshroom.unplanned.pointer.Pointer;
import com.techshroom.unplanned.value.Dimension;
import com.techshroom.unplanned.value.Point;

/**
 * Window object for a more object oriented GLFW.
 * 
 * @author Kenzie Togami
 */
public interface Window {

    /**
     * Window callback interface.
     * 
     * @author Kenzie Togami
     */
    interface OnCloseCallback {

        /**
         * Called when {@code window} is closed.
         * 
         * @param window
         *            - window reference
         */
        void onWindowClose(Window window);

    }

    /**
     * Window callback interface.
     * 
     * @author Kenzie Togami
     */
    interface OnMoveCallback {

        /**
         * Called when {@code window} is moved.
         * 
         * @param window
         *            - window reference
         * @param newX
         *            - new window X
         * @param newY
         *            - new window Y
         */
        void onWindowMove(Window window, int newX, int newY);

    }

    /**
     * Window callback interface.
     * 
     * @author Kenzie Togami
     */
    interface OnResizeCallback {

        /**
         * Called when {@code window} is resized.
         * 
         * @param window
         *            - window reference
         * @param newWidth
         *            - new window width
         * @param newHeight
         *            - new window height
         */
        void onWindowResize(Window window, int newWidth, int newHeight);

    }

    /**
     * Window callback interface.
     * 
     * @author Kenzie Togami
     */
    interface OnResizeFramebufferCallback {

        /**
         * Called when {@code window}'s framebuffer is resized.
         * 
         * @param window
         *            - window reference
         * @param newWidth
         *            - new framebuffer width
         * @param newHeight
         *            - new framebuffer height
         */
        void onWindowFramebufferResize(Window window, int newWidth,
                int newHeight);

    }

    /**
     * Window callback interface.
     * 
     * @author Kenzie Togami
     */
    interface OnFocusCallback {

        /**
         * Called when {@code window}'s focus status changes.
         * 
         * @param window
         *            - window reference
         * @param focused
         *            - The window's focus status
         */
        void onWindowFocusChange(Window window, boolean focused);

    }

    /**
     * Window callback interface.
     * 
     * @author Kenzie Togami
     */
    interface OnMinimizeChangeCallback {

        /**
         * Called when {@code window}'s minimize status changes.
         * 
         * @param window
         *            - window reference
         * @param minimized
         *            - The window's minimize status
         */
        void onWindowMinimizeChange(Window window, boolean minimized);

    }

    /**
     * Window callback interface.
     * 
     * @author Kenzie Togami
     */
    interface OnRefreshRequestedCallback {

        /**
         * Called when {@code window} needs to be refreshed.
         * 
         * @param window
         *            - window reference
         */
        void onWindowRefreshRequested(Window window);

    }

    Dimension getSize();

    Dimension getFramebufferSize();

    Point getLocation();

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
     * @return a {@link Pointer} wrapper for the window pointer
     */
    Pointer getWindowPointer();

    void setSize(Dimension size);

    void setVsyncOn(boolean on);

    void setCloseRequested(boolean requested);

    void setMinimized(boolean minimized);

    void setVisible(boolean visible);

    void setTitle(String title);

    GraphicsContext getGraphicsContext();

    void destroy();

    void processEvents();

    void waitForEvents();

    /**
     * Add a callback for when the Window closes.
     * 
     * @param callback
     *            - the callback
     */
    void onClose(OnCloseCallback callback);

    /**
     * Add a callback for when the Window moves.
     * 
     * @param callback
     *            - the callback
     */
    void onMove(OnMoveCallback callback);

    /**
     * Add a callback for when the Window resizes.
     * 
     * @param callback
     *            - the callback
     */
    void onResize(OnResizeCallback callback);

    /**
     * Add a callback for when the Window framebuffer resizes.
     * 
     * @param callback
     *            - the callback
     */
    void onResizeFramebuffer(OnResizeFramebufferCallback callback);

    /**
     * Add a callback for when the Window's focus status changes.
     * 
     * @param callback
     *            - the callback
     */
    void onFocusChange(OnFocusCallback callback);

    /**
     * Add a callback for when the Window's minimize status changes.
     * 
     * @param callback
     *            - the callback
     */
    void onMinimizeChange(OnMinimizeChangeCallback callback);

    /**
     * Add a callback for when the Window is told to refresh.
     * 
     * @param callback
     *            - the callback
     */
    void onRefreshRequested(OnRefreshRequestedCallback callback);

}
