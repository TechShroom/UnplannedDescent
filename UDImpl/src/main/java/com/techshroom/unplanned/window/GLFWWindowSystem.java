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

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;

import java.util.List;

import org.lwjgl.PointerBuffer;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.core.util.GLFWUtil;

@AutoService(WindowSystem.class)
public class GLFWWindowSystem implements WindowSystem {

    static {
        GLFWUtil.ensureInitialized();
    }

    @Override
    public Window getActiveWindow() throws IllegalStateException {
        long active = glfwGetCurrentContext();
        if (active == 0) {
            throw new IllegalStateException("No active window");
        }
        return GLFWWindow.get(active);
    }

    @Override
    public Monitor getPrimaryMonitor() {
        return GLFWMonitor.getMonitor(glfwGetPrimaryMonitor());
    }

    @Override
    public List<Monitor> getMonitors() {
        PointerBuffer ptrs = glfwGetMonitors();
        ImmutableList.Builder<Monitor> list = ImmutableList.builder();
        while (ptrs.hasRemaining()) {
            list.add(GLFWMonitor.getMonitor(ptrs.get()));
        }
        return list.build();
    }

}
