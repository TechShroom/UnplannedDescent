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
package com.techshroom.unplanned.input;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import java.nio.DoubleBuffer;

import org.lwjgl.system.MemoryStack;

import com.flowpowered.math.vector.Vector2d;
import com.google.common.eventbus.EventBus;
import com.techshroom.unplanned.event.mouse.MouseButtonEvent;
import com.techshroom.unplanned.event.mouse.MouseMoveEvent;
import com.techshroom.unplanned.event.mouse.MouseScrollEvent;
import com.techshroom.unplanned.window.GLFWWindow;

public class GLFWMouse implements Mouse {

    private final GLFWWindow window;

    public GLFWMouse(GLFWWindow window) {
        this.window = window;

        EventBus bus = this.window.getEventBus();
        long ptr = window.getWindowPointer();
        glfwSetCursorPosCallback(ptr, (win, x, y) -> {
            if (win != this.window.getWindowPointer()) {
                return;
            }
            bus.post(MouseMoveEvent.create(this, x, y));
        });
        glfwSetMouseButtonCallback(ptr, (win, button, action, mods) -> {
            if (win != this.window.getWindowPointer()) {
                return;
            }
            bus.post(MouseButtonEvent.create(this, button, action == GLFW_PRESS, GLFWKeyboard.getModifiers(mods)));
        });
        glfwSetScrollCallback(ptr, (win, dx, dy) -> {
            if (win != this.window.getWindowPointer()) {
                return;
            }
            bus.post(MouseScrollEvent.create(this, dx, dy));
        });
    }

    @Override
    public Vector2d getPosition() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer xpos = stack.mallocDouble(1);
            DoubleBuffer ypos = stack.mallocDouble(1);
            glfwGetCursorPos(window.getWindowPointer(), xpos, ypos);
            return new Vector2d(xpos.get(0), ypos.get(0));
        }
    }

    @Override
    public void activateMouseGrab() {
        glfwSetInputMode(window.getWindowPointer(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    @Override
    public void deactivateMouseGrab() {
        glfwSetInputMode(window.getWindowPointer(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

}
