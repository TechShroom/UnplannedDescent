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
import static org.lwjgl.glfw.GLFW.GLFW_COCOA_RETINA_FRAMEBUFFER;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import org.lwjgl.system.Platform;

import com.google.auto.service.AutoService;
import com.techshroom.unplanned.core.util.GLFWUtil;
import com.techshroom.unplanned.monitor.Monitor;
import com.techshroom.unplanned.monitor.MonitorProvider;
import com.techshroom.unplanned.pointer.Pointer;
import com.techshroom.unplanned.pointer.PointerImpl;
import com.techshroom.unplanned.value.Dimension;
import com.techshroom.unplanned.value.VideoMode;

@AutoService(WindowGenerator.class)
public class GLFWWindowGenerator implements WindowGenerator {

    static {
        GLFWUtil.ensureInitialized();
    }

    @Override
    public Dimension getDefaultFullscreenSize() {
        Monitor primary = MonitorProvider.getInstance().getPrimaryMonitor();
        VideoMode videoMode = primary.getVideoMode();
        return Dimension.of(videoMode.getWidth(), videoMode.getHeight());
    }

    @Override
    public Window generateWindow(WindowSettings settings) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, settings.isResizable() ? GLFW_TRUE : GLFW_FALSE);

        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
        }
        
        // setup core context
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        long window = glfwCreateWindow(settings.getScreenSize().getWidth(), settings.getScreenSize().getHeight(), settings.getTitle(),
                monitorIfNeeded(settings), sharedWindow(settings));
        return new GLFWWindow(PointerImpl.wrap(window));
    }

    private long monitorIfNeeded(WindowSettings settings) {
        if (settings.isFullScreen()) {
            checkState(settings.getMonitor().isPresent(), "A monitor is required for fullscreen");
            return settings.getMonitor().get().getMonitorPointer().address();
        }
        return 0;
    }

    private long sharedWindow(WindowSettings settings) {
        return settings.getSharedWindow().map(Window::getWindowPointer).map(Pointer::address).orElse(0L);
    }

}
