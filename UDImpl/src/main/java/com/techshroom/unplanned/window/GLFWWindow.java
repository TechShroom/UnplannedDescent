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
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwRestoreWindow;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWaitEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import com.flowpowered.math.vector.Vector2i;
import com.techshroom.unplanned.blitter.GLGraphicsContext;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.core.util.GLFWUtil;
import com.techshroom.unplanned.event.Event;
import com.techshroom.unplanned.event.window.WindowResizeEvent;
import com.techshroom.unplanned.input.GLFWKeyboard;
import com.techshroom.unplanned.input.GLFWMouse;
import com.techshroom.unplanned.input.Keyboard;
import com.techshroom.unplanned.input.Mouse;
import com.techshroom.unplanned.monitor.GLFWMonitor;
import com.techshroom.unplanned.monitor.Monitor;
import com.techshroom.unplanned.pointer.Pointer;

public class GLFWWindow implements Window {

    static {
        GLFWUtil.ensureInitialized();
    }

    private final GLGraphicsContext graphicsContext;
    private final Mouse mouse;
    private final Keyboard keyboard;
    private final Pointer windowPtr;
    private String title;
    private boolean vsync;
    private boolean destroyed;

    GLFWWindow(Pointer windowPtr) {
        this.windowPtr = windowPtr;
        this.graphicsContext = new GLGraphicsContext(this);
        this.mouse = new GLFWMouse(windowPtr.address());
        this.keyboard = new GLFWKeyboard(windowPtr.address());

        setupEventPublishers();

        // setup at-exit hook to kill
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroy, "GLFW Window destructor " + windowPtr.address()));
    }

    private void setupEventPublishers() {
        long window = windowPtr.address();

        glfwSetWindowSizeCallback(window, (win, w, h) -> {
            Event.BUS.post(WindowResizeEvent.create(this, w, h));
        });
        glfwSetFramebufferSizeCallback(window, (win, w, h) -> {
            Event.BUS.post(WindowResizeEvent.create(this, w, h));
        });
    }

    @Override
    public Vector2i getSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetWindowSize(this.windowPtr.address(), width, height);
            return new Vector2i(width.get(0), height.get(0));
        }
    }

    @Override
    public Vector2i getFramebufferSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetFramebufferSize(this.windowPtr.address(), width, height);
            return new Vector2i(width.get(0), height.get(0));
        }
    }

    @Override
    public Vector2i getLocation() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            glfwGetWindowPos(this.windowPtr.address(), x, y);
            return new Vector2i(x.get(0), y.get(0));
        }
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
    public void setSize(Vector2i size) {
        glfwSetWindowSize(this.windowPtr.address(), size.getX(), size.getY());
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

}
