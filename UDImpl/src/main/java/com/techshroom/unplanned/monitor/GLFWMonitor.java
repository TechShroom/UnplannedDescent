package com.techshroom.unplanned.monitor;

import static com.google.common.base.Preconditions.checkState;
import static org.lwjgl.glfw.GLFW.GLFWMonitorCallback;
import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.GLFW_DISCONNECTED;
import static org.lwjgl.glfw.GLFW.glfwGetGammaRamp;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorName;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorPos;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;
import static org.lwjgl.glfw.GLFW.glfwSetGammaRamp;
import static org.lwjgl.glfw.GLFW.glfwSetMonitorCallback;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.lwjgl.BufferUtils;
import org.lwjgl.Pointer;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWgammaramp;
import org.lwjgl.glfw.GLFWvidmode;

import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.core.util.GLFWUtil;
import com.techshroom.unplanned.pointer.PointerImpl;
import com.techshroom.unplanned.value.GammaRamp;
import com.techshroom.unplanned.value.Point;
import com.techshroom.unplanned.value.VideoMode;

public class GLFWMonitor implements Monitor {

    private static final Map<Long, GLFWMonitor> monitorCache = new HashMap<>();

    private static enum CallbackHandler implements GLFWMonitorCallback.SAM {

        INSTANCE;

        private GLFWMonitorCallback actualCallback;

        @Override
        public void invoke(long monitor, int event) {
            this.actualCallback.invoke(monitor, event);
            if (event == GLFW_DISCONNECTED) {
                // clear from cache
                monitorCache.remove(monitor);
            }
        }

    }

    static {
        GLFWUtil.ensureInitialized();
        glfwSetMonitorCallback(GLFWMonitorCallback(CallbackHandler.INSTANCE));
    }

    private static final VideoMode convertVidMode(GLFWvidmode mode) {
        return VideoMode.create(mode.getWidth(), mode.getHeight(),
                mode.getRedBits(), mode.getGreenBits(), mode.getBlueBits(),
                mode.getRefreshRate());
    }

    private static final GammaRamp convertGammaramp(GLFWgammaramp ramp) {
        int size = ramp.getSize();
        ShortBuffer tmp;
        short[] tmpShorts = new short[size];
        tmp = ramp.getRed(size).asShortBuffer();
        tmp.get(tmpShorts);
        int[] red = unsignShorts(tmpShorts);
        tmp = ramp.getGreen(size).asShortBuffer();
        tmp.get(tmpShorts);
        int[] green = unsignShorts(tmpShorts);
        tmp = ramp.getBlue(size).asShortBuffer();
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

    private static final ByteBuffer convertGammaRamp(GammaRamp ramp) {
        GLFWgammaramp tmp = new GLFWgammaramp();
        tmp.setSize(ramp.getSize());
        ByteBuffer red = BufferUtils.createByteBuffer(ramp.getSize());
        red.asShortBuffer().put(resignShorts(ramp.getRedChannel()));
        tmp.setRed(red);
        ByteBuffer green = BufferUtils.createByteBuffer(ramp.getSize());
        green.asShortBuffer().put(resignShorts(ramp.getGreenChannel()));
        tmp.setGreen(green);
        ByteBuffer blue = BufferUtils.createByteBuffer(ramp.getSize());
        blue.asShortBuffer().put(resignShorts(ramp.getBlueChannel()));
        tmp.setBlue(blue);
        return tmp.buffer();
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

    private final Pointer monitorPointer;
    private final IntBuffer supportedVideoModesCount = BufferUtils
            .createIntBuffer(1);
    private final IntBuffer locationX = BufferUtils.createIntBuffer(1);
    private final IntBuffer locationY = BufferUtils.createIntBuffer(1);

    private GLFWMonitor(long monitorPointer) {
        this.monitorPointer = PointerImpl.wrap(monitorPointer);
    }

    @Override
    public List<VideoMode> getSupportedVideoModes() {
        ByteBuffer videoModes =
                glfwGetVideoModes(this.monitorPointer.getPointer(),
                        this.supportedVideoModesCount);
        int count = this.supportedVideoModesCount.get(0);
        checkState(count != 0, "error");
        GLFWvidmode mode = new GLFWvidmode(videoModes);
        ImmutableList.Builder<VideoMode> list = ImmutableList.builder();
        for (int j = 0; j < count; j++) {
            videoModes.position(j * GLFWvidmode.SIZEOF);
            list.add(convertVidMode(mode));
        }
        return list.build();
    }

    @Override
    public VideoMode getVideoMode() {
        return convertVidMode(new GLFWvidmode(
                glfwGetVideoMode(this.monitorPointer.getPointer())));
    }

    @Override
    public String getTitle() {
        return glfwGetMonitorName(this.monitorPointer.getPointer());
    }

    @Override
    public Point getLocation() {
        glfwGetMonitorPos(this.monitorPointer.getPointer(), this.locationX,
                this.locationY);
        return Point.of(this.locationX.get(0), this.locationY.get(0));
    }

    @Override
    public GammaRamp getGammaRamp() {
        return convertGammaramp(new GLFWgammaramp(
                glfwGetGammaRamp(this.monitorPointer.getPointer())));
    }

    @Override
    public void setGammaRamp(GammaRamp ramp) {
        glfwSetGammaRamp(this.monitorPointer.getPointer(),
                convertGammaRamp(ramp));
    }

    @Override
    public void setMonitorCallback(BiConsumer<Monitor, Event> callback) {
        CallbackHandler.INSTANCE.actualCallback =
                GLFWMonitorCallback((monitorPtr, event) -> {
                    checkState(monitorPtr == this.monitorPointer.getPointer(),
                            "wrong window?");
                    callback.accept(this,
                            event == GLFW_CONNECTED ? Event.CONNECTED
                                    : Event.DISCONNECTED);
                });
    }

    @Override
    public Pointer getMonitorPointer() {
        return this.monitorPointer;
    }

}
