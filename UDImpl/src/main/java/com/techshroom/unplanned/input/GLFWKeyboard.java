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

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.techshroom.unplanned.event.keyboard.KeyState;
import com.techshroom.unplanned.event.keyboard.KeyStateEvent;
import com.techshroom.unplanned.window.GLFWWindow;

public class GLFWKeyboard implements Keyboard {

    private static final BiMap<Key, Integer> UD_KEY_TO_GLFW = readGlfwKeys();
    private static final Map<Integer, KeyModifier> GLFW_TO_KEY_MOD = readGlfwMods();

    private static BiMap<Key, Integer> readGlfwKeys() {
        ImmutableBiMap.Builder<Key, Integer> b = ImmutableBiMap.builder();

        for (Key k : Key.values()) {
            Field glfwKeyField;
            try {
                glfwKeyField = GLFW.class.getDeclaredField("GLFW_KEY_" + k.name().replaceFirst("NUM_(\\d)", "$1"));
                b.put(k, glfwKeyField.getInt(null));
            } catch (Exception e) {
                throw new RuntimeException("unexpected error reading key fields", e);
            }
        }

        return b.build();
    }

    private static Map<Integer, KeyModifier> readGlfwMods() {
        ImmutableMap.Builder<Integer, KeyModifier> b = ImmutableMap.builder();

        for (KeyModifier km : KeyModifier.values()) {
            Field glfwModField;
            try {
                glfwModField = GLFW.class.getDeclaredField("GLFW_MOD_" + km.name());
                b.put(glfwModField.getInt(null), km);
            } catch (Exception e) {
                throw new RuntimeException("unexpected error reading mod fields", e);
            }
        }

        return b.build();
    }

    public static Collection<KeyModifier> getModifiers(int mods) {
        ImmutableSet.Builder<KeyModifier> b = ImmutableSet.builder();
        GLFW_TO_KEY_MOD.forEach((k, km) -> {
            if ((k & mods) != 0) {
                b.add(km);
            }
        });
        return b.build();
    }

    private static KeyState getKeyState(int action) {
        switch (action) {
            case GLFW_PRESS:
                return KeyState.PRESSED;
            case GLFW_RELEASE:
                return KeyState.RELEASED;
            case GLFW_REPEAT:
                return KeyState.REPEATED;
            default:
                throw new IllegalArgumentException("unknown state: " + action);
        }
    }

    private final GLFWWindow window;

    public GLFWKeyboard(GLFWWindow window) {
        this.window = window;

        // register key callback
        glfwSetKeyCallback(window.getWindowPointer(), this::keyCallback);
    }

    @Override
    public boolean isKeyDown(Key key) {
        return glfwGetKey(window.getWindowPointer(), UD_KEY_TO_GLFW.get(key)) == GLFW_PRESS;
    }

    private void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (window != this.window.getWindowPointer()) {
            return;
        }
        Key k = UD_KEY_TO_GLFW.inverse().get(key);
        KeyState state = getKeyState(action);
        Collection<KeyModifier> modSet = getModifiers(mods);

        this.window.getEventBus().post(KeyStateEvent.create(this, k, state, modSet));
    }

}
