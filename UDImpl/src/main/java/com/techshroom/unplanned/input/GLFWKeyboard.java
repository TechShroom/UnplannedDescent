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
package com.techshroom.unplanned.input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class GLFWKeyboard implements Keyboard {

    private static final BiMap<Key, Integer> TO_GLFW = readGlfwKeys();
    private static final Map<Integer, KeyModifier> FROM_GLFW = readGlfwMods();

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

    private final long window;
    private final Map<Key, KeyListener> listeners = new EnumMap<>(Key.class);
    private final Set<Key> tappingKeys = EnumSet.noneOf(Key.class);

    public GLFWKeyboard(long window) {
        this.window = window;

        // register key callback
        glfwSetKeyCallback(window, this::keyCallback);
    }

    @Override
    public void addKeyListener(Key key, KeyListener keyListener) {
        listeners.put(key, keyListener);
    }

    private void keyCallback(long window, int key, int scancode, int action, int mods) {
        Key k = TO_GLFW.inverse().get(key);
        KeyListener kl = listeners.get(k);
        if (kl == null) {
            return;
        }
        KeyEvent event = KeyEvent.create(getModifiers(mods));
        switch (action) {
            case GLFW_PRESS:
                tappingKeys.add(k);
                kl.onPressed(event);
                break;
            case GLFW_RELEASE:
                kl.onReleased(event);
                if (tappingKeys.remove(k)) {
                    kl.onTapped(event);
                }
                break;
            case GLFW_REPEAT:
                // cannot tap when repeated
                tappingKeys.remove(k);
                kl.onRepeated(event);
                break;
            default:
                throw new IllegalArgumentException("unexpected action " + action);
        }
    }

    private Collection<KeyModifier> getModifiers(int mods) {
        ImmutableSet.Builder<KeyModifier> b = ImmutableSet.builder();
        FROM_GLFW.forEach((k, km) -> {
            if ((k & mods) != 0) {
                b.add(km);
            }
        });
        return b.build();
    }

}
