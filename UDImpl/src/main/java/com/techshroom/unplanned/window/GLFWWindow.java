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

import static com.google.common.base.Preconditions.checkState;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetClipboardString;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwGetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwRestoreWindow;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWaitEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.system.MemoryStack;

import com.techshroom.unplanned.blitter.GLGraphicsContext;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.core.util.GLFWUtil;
import com.techshroom.unplanned.input.GLFWKeyboard;
import com.techshroom.unplanned.input.GLFWMouse;
import com.techshroom.unplanned.input.Keyboard;
import com.techshroom.unplanned.input.Mouse;
import com.techshroom.unplanned.monitor.GLFWMonitor;
import com.techshroom.unplanned.monitor.Monitor;
import com.techshroom.unplanned.pointer.Pointer;
import com.techshroom.unplanned.value.Dimension;
import com.techshroom.unplanned.value.Point;

public class GLFWWindow implements Window {

    static {
        GLFWUtil.ensureInitialized();
    }

    private final GraphicsContext graphicsContext;
    private final Mouse mouse;
    private final Keyboard keyboard;
    private final Pointer windowPtr;
    private String title;
    private boolean vsync;
    private Point location;
    private boolean destroyed;

    GLFWWindow(Pointer windowPtr) {
        this.windowPtr = windowPtr;
        this.graphicsContext = new GLGraphicsContext(windowPtr.address());
        this.mouse = new GLFWMouse(windowPtr.address());
        this.keyboard = new GLFWKeyboard(windowPtr.address());

        // setup at-exit hook to kill
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroy, "GLFW Window destructor " + windowPtr.address()));
    }

    @Override
    public Dimension getSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetWindowSize(this.windowPtr.address(), width, height);
            return Dimension.of(width.get(0), height.get(0));
        }
    }

    @Override
    public Dimension getFramebufferSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetFramebufferSize(this.windowPtr.address(), width, height);
            return Dimension.of(width.get(0), height.get(0));
        }
    }

    @Override
    public Point getLocation() {
        return this.location;
    }

    @Override
    public Mouse getMouse() {
        return mouse;
    }

    @Override
    public Keyboard getKeyboard() {
        return keyboard;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public Monitor getMonitor() {
        return GLFWMonitor
                .getMonitor(glfwGetWindowMonitor(this.windowPtr.address()));
    }

    @Override
    public String getClipboardContents() {
        return glfwGetClipboardString(this.windowPtr.address());
    }

    @Override
    public boolean isVsyncOn() {
        return this.vsync;
    }

    @Override
    public boolean isCloseRequested() {
        return glfwWindowShouldClose(this.windowPtr.address());
    }

    @Override
    public boolean isVisible() {
        return glfwGetWindowAttrib(this.windowPtr.address(), GLFW_VISIBLE) == GLFW_TRUE;
    }

    @Override
    @Deprecated
    public int getAttribute(int attr) {
        return glfwGetWindowAttrib(this.windowPtr.address(), attr);
    }

    @Override
    public Pointer getWindowPointer() {
        return this.windowPtr;
    }

    @Override
    public void setSize(Dimension size) {
        glfwSetWindowSize(this.windowPtr.address(), size.getWidth(),
                size.getHeight());
    }

    @Override
    public void setVsyncOn(boolean on) {
        checkState(glfwGetCurrentContext() == this.windowPtr.address(), "Not the active window!");
        this.vsync = true;
        glfwSwapInterval(1);
    }

    @Override
    public void setCloseRequested(boolean requested) {
        glfwSetWindowShouldClose(this.windowPtr.address(), requested);
    }

    @Override
    public void setMinimized(boolean minimized) {
        if (minimized) {
            glfwIconifyWindow(this.windowPtr.address());
        } else {
            glfwRestoreWindow(this.windowPtr.address());
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            glfwShowWindow(this.windowPtr.address());
        } else {
            glfwHideWindow(this.windowPtr.address());
        }
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(this.windowPtr.address(), title);
    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    @Override
    public void processEvents() {
        glfwPollEvents();
    }

    @Override
    public void waitForEvents() {
        glfwWaitEvents();
    }

    @Override
    public void destroy() {
        if (destroyed) {
            return;
        }
        this.destroyed = true;
        glfwFreeCallbacks(this.windowPtr.address());
        glfwDestroyWindow(this.windowPtr.address());
    }

    private void checkWindow(long window) {
        checkState(window == this.windowPtr.address(), "incorrect window");
    }

    @Override
    public void onClose(OnCloseCallback callback) {
        glfwSetWindowCloseCallback(this.windowPtr.address(),
                GLFWWindowCloseCallback.create(window -> {
                    checkWindow(window);
                    callback.onWindowClose(this);
                }));
    }

    @Override
    public void onMove(OnMoveCallback callback) {
        glfwSetWindowPosCallback(this.windowPtr.address(),
                GLFWWindowPosCallback.create((window, x, y) -> {
                    checkWindow(window);
                    callback.onWindowMove(this, x, y);
                }));
    }

    @Override
    public void onResize(OnResizeCallback callback) {
        glfwSetWindowSizeCallback(this.windowPtr.address(),
                GLFWWindowSizeCallback.create((window, width, height) -> {
                    checkWindow(window);
                    callback.onWindowResize(this, width, height);
                }));
    }

    @Override
    public void onResizeFramebuffer(OnResizeFramebufferCallback callback) {
        glfwSetFramebufferSizeCallback(this.windowPtr.address(),
                GLFWFramebufferSizeCallback.create((window, width, height) -> {
                    checkWindow(window);
                    callback.onWindowFramebufferResize(this, width, height);
                }));
    }

    @Override
    public void onFocusChange(OnFocusCallback callback) {
        glfwSetWindowFocusCallback(this.windowPtr.address(),
                GLFWWindowFocusCallback.create((window, focused) -> {
                    checkWindow(window);
                    callback.onWindowFocusChange(this, focused);
                }));
    }

    @Override
    public void onMinimizeChange(OnMinimizeChangeCallback callback) {
        glfwSetWindowIconifyCallback(this.windowPtr.address(),
                GLFWWindowIconifyCallback.create((window, minimized) -> {
                    checkWindow(window);
                    callback.onWindowMinimizeChange(this,
                            minimized);
                }));
    }

    @Override
    public void onRefreshRequested(OnRefreshRequestedCallback callback) {
        glfwSetWindowRefreshCallback(this.windowPtr.address(),
                GLFWWindowRefreshCallback.create((window) -> {
                    checkWindow(window);
                    callback.onWindowRefreshRequested(this);
                }));
    }

}
