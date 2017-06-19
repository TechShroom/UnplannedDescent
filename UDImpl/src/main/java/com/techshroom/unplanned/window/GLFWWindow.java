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
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryStack;

import com.flowpowered.math.vector.Vector2i;
import com.google.common.eventbus.EventBus;
import com.techshroom.unplanned.blitter.GLGraphicsContext;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.core.util.GLFWUtil;
import com.techshroom.unplanned.event.window.WindowResizeEvent;
import com.techshroom.unplanned.input.GLFWKeyboard;
import com.techshroom.unplanned.input.GLFWMouse;
import com.techshroom.unplanned.input.Keyboard;
import com.techshroom.unplanned.input.Mouse;

public class GLFWWindow implements Window {

    static {
        GLFWUtil.ensureInitialized();
    }

    private static final Map<Long, GLFWWindow> windows = new HashMap<>();

    public static GLFWWindow get(long pointer) {
        return windows.computeIfAbsent(pointer, GLFWWindow::new);
    }

    public static GLFWWindow get(long pointer, WindowSettings settings) {
        GLFWWindow window = get(pointer);
        window.settings = settings;
        return window;
    }

    private final EventBus eventBus;
    private final GLGraphicsContext graphicsContext;
    private final Mouse mouse;
    private final Keyboard keyboard;
    private final long pointer;
    private WindowSettings settings;
    private String title;
    private boolean vsync;
    private boolean destroyed;

    private GLFWWindow(long pointer) {
        this.pointer = pointer;
        this.eventBus = new EventBus("window-0x" + Long.toHexString(pointer));
        this.graphicsContext = new GLGraphicsContext(this);
        this.mouse = new GLFWMouse(this);
        this.keyboard = new GLFWKeyboard(this);

        setupEventPublishers();

        // setup at-exit hook to kill
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroy, "GLFW Window destructor " + pointer));
    }

    private void setupEventPublishers() {
        glfwSetWindowSizeCallback(pointer, (win, w, h) -> {
            eventBus.post(WindowResizeEvent.create(this, w, h));
        });
        glfwSetFramebufferSizeCallback(pointer, (win, w, h) -> {
            eventBus.post(WindowResizeEvent.create(this, w, h));
        });
    }

    public WindowSettings getSettings() {
        return settings;
    }

    @Override
    public EventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public Vector2i getSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetWindowSize(pointer, width, height);
            return new Vector2i(width.get(0), height.get(0));
        }
    }

    @Override
    public Vector2i getFramebufferSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetFramebufferSize(pointer, width, height);
            return new Vector2i(width.get(0), height.get(0));
        }
    }

    @Override
    public Vector2i getLocation() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            glfwGetWindowPos(pointer, x, y);
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
                .getMonitor(glfwGetWindowMonitor(pointer));
    }

    @Override
    public String getClipboardContents() {
        return glfwGetClipboardString(pointer);
    }

    @Override
    public boolean isVsyncOn() {
        return this.vsync;
    }

    @Override
    public boolean isCloseRequested() {
        return glfwWindowShouldClose(pointer);
    }

    @Override
    public boolean isVisible() {
        return glfwGetWindowAttrib(pointer, GLFW_VISIBLE) == GLFW_TRUE;
    }

    @Override
    @Deprecated
    public int getAttribute(int attr) {
        return glfwGetWindowAttrib(pointer, attr);
    }

    @Override
    public long getWindowPointer() {
        return this.pointer;
    }

    @Override
    public void setSize(Vector2i size) {
        glfwSetWindowSize(pointer, size.getX(), size.getY());
    }

    @Override
    public void setVsyncOn(boolean on) {
        checkState(glfwGetCurrentContext() == pointer, "Not the active window!");
        this.vsync = true;
        glfwSwapInterval(1);
    }

    @Override
    public void setCloseRequested(boolean requested) {
        glfwSetWindowShouldClose(pointer, requested);
    }

    @Override
    public void setMinimized(boolean minimized) {
        if (minimized) {
            glfwIconifyWindow(pointer);
        } else {
            glfwRestoreWindow(pointer);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            glfwShowWindow(pointer);
        } else {
            glfwHideWindow(pointer);
        }
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(pointer, title);
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
        glfwFreeCallbacks(pointer);
        glfwDestroyWindow(pointer);
        windows.remove(pointer);
    }

}
