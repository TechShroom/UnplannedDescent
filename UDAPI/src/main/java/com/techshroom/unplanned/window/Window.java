package com.techshroom.unplanned.window;

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

    Point getCursorLocation();

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

    void destroy();

    void swapBuffers();

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
