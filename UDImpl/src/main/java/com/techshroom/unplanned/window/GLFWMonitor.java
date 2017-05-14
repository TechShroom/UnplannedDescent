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
import static org.lwjgl.glfw.GLFW.glfwGetGammaRamp;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorName;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorPos;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;
import static org.lwjgl.glfw.GLFW.glfwSetGammaRamp;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWGammaRamp;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import com.flowpowered.math.vector.Vector2i;
import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.core.util.GLFWUtil;
import com.techshroom.unplanned.value.GammaRamp;
import com.techshroom.unplanned.value.VideoMode;

public class GLFWMonitor implements Monitor {

    private static final Map<Long, GLFWMonitor> monitorCache = new HashMap<>();

    static {
        GLFWUtil.ensureInitialized();
    }

    private static final VideoMode convertVidMode(GLFWVidMode mode) {
        return VideoMode.create(mode.width(), mode.height(), mode.redBits(),
                mode.greenBits(), mode.blueBits(), mode.refreshRate());
    }

    private static final GammaRamp convertGammaramp(GLFWGammaRamp ramp) {
        int size = ramp.size();
        ShortBuffer tmp;
        short[] tmpShorts = new short[size];
        tmp = ramp.red();
        tmp.get(tmpShorts);
        int[] red = unsignShorts(tmpShorts);
        tmp = ramp.green();
        tmp.get(tmpShorts);
        int[] green = unsignShorts(tmpShorts);
        tmp = ramp.blue();
        tmp.get(tmpShorts);
        int[] blue = unsignShorts(tmpShorts);
        return GammaRamp.of(red, green, blue, size);
    }

    private static final int[] unsignShorts(short[] shorts) {
        int[] out = new int[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            // unsign-short
            out[i] = shorts[i] & 0xFFFF;
        }
        return out;
    }

    private static final short[] resignShorts(int[] shorts) {
        short[] out = new short[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            // resign-short
            out[i] = (short) (shorts[i] & 0xFFFF);
        }
        return out;
    }

    private static final GLFWGammaRamp convertGammaRamp(GammaRamp ramp) {
        GLFWGammaRamp tmp = GLFWGammaRamp.create();
        tmp.size(ramp.getSize());
        ShortBuffer red = BufferUtils.createShortBuffer(ramp.getSize());
        red.put(resignShorts(ramp.getRedChannel().data));
        tmp.red(red);
        ShortBuffer green = BufferUtils.createShortBuffer(ramp.getSize());
        green.put(resignShorts(ramp.getGreenChannel().data));
        tmp.green(green);
        ShortBuffer blue = BufferUtils.createShortBuffer(ramp.getSize());
        blue.put(resignShorts(ramp.getBlueChannel().data));
        tmp.blue(blue);
        return tmp;
    }

    /**
     * Because saving monitor objects is good.
     * 
     * @param pointer
     *            - The raw long pointer that points to the window
     * @return The monitor object that provides information about the pointer's
     *         monitor
     */
    public static final GLFWMonitor getMonitor(long pointer) {
        return monitorCache.computeIfAbsent(pointer, GLFWMonitor::new);
    }

    private final long pointer;

    private GLFWMonitor(long pointer) {
        this.pointer = pointer;
    }

    @Override
    public List<VideoMode> getSupportedVideoModes() {
        GLFWVidMode.Buffer videoModes =
                glfwGetVideoModes(pointer);
        int count = videoModes.capacity();
        checkState(count != 0, "error");
        ImmutableList.Builder<VideoMode> list = ImmutableList.builder();
        for (int j = 0; j < count; j++) {
            list.add(convertVidMode(videoModes.get()));
        }
        return list.build();
    }

    @Override
    public VideoMode getVideoMode() {
        return convertVidMode(glfwGetVideoMode(pointer));
    }

    @Override
    public String getTitle() {
        return glfwGetMonitorName(pointer);
    }

    @Override
    public Vector2i getLocation() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            glfwGetMonitorPos(pointer, x, y);
            return new Vector2i(x.get(0), y.get(0));
        }
    }

    @Override
    public GammaRamp getGammaRamp() {
        return convertGammaramp(
                glfwGetGammaRamp(pointer));
    }

    @Override
    public void setGammaRamp(GammaRamp ramp) {
        glfwSetGammaRamp(pointer, convertGammaRamp(ramp));
    }

    @Override
    public long getMonitorPointer() {
        return pointer;
    }

}
