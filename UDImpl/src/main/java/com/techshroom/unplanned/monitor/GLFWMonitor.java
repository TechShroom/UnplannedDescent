package com.techshroom.unplanned.monitor;

import static com.google.common.base.Preconditions.checkState;
import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.GLFW_DISCONNECTED;
import static org.lwjgl.glfw.GLFW.glfwGetGammaRamp;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorName;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorPos;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;
import static org.lwjgl.glfw.GLFW.glfwSetGammaRamp;
import static org.lwjgl.glfw.GLFW.glfwSetMonitorCallback;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWGammaRamp;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;

import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.core.util.GLFWUtil;
import com.techshroom.unplanned.pointer.DualPointer;
import com.techshroom.unplanned.pointer.Pointer;
import com.techshroom.unplanned.value.GammaRamp;
import com.techshroom.unplanned.value.Point;
import com.techshroom.unplanned.value.VideoMode;

public class GLFWMonitor implements Monitor {

    private static final Map<Long, GLFWMonitor> monitorCache = new HashMap<>();

    private static enum CallbackHandler implements GLFWMonitorCallbackI {

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
        glfwSetMonitorCallback(
                GLFWMonitorCallback.create(CallbackHandler.INSTANCE));
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

    private final Pointer monitorPointer;
    private final IntBuffer locationX = BufferUtils.createIntBuffer(1);
    private final IntBuffer locationY = BufferUtils.createIntBuffer(1);

    private GLFWMonitor(long monitorPointer) {
        this.monitorPointer = DualPointer.wrap(monitorPointer);
    }

    @Override
    public List<VideoMode> getSupportedVideoModes() {
        GLFWVidMode.Buffer videoModes =
                glfwGetVideoModes(this.monitorPointer.address());
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
        return convertVidMode(glfwGetVideoMode(this.monitorPointer.address()));
    }

    @Override
    public String getTitle() {
        return glfwGetMonitorName(this.monitorPointer.address());
    }

    @Override
    public Point getLocation() {
        glfwGetMonitorPos(this.monitorPointer.address(), this.locationX,
                this.locationY);
        return Point.of(this.locationX.get(0), this.locationY.get(0));
    }

    @Override
    public GammaRamp getGammaRamp() {
        return convertGammaramp(
                glfwGetGammaRamp(this.monitorPointer.address()));
    }

    @Override
    public void setGammaRamp(GammaRamp ramp) {
        glfwSetGammaRamp(this.monitorPointer.address(), convertGammaRamp(ramp));
    }

    @Override
    public void setMonitorCallback(BiConsumer<Monitor, Event> callback) {
        CallbackHandler.INSTANCE.actualCallback =
                GLFWMonitorCallback.create((monitorPtr, event) -> {
                    checkState(monitorPtr == this.monitorPointer.address(),
                            "wrong window?");
                    callback.accept(this, event == GLFW_CONNECTED
                            ? Event.CONNECTED : Event.DISCONNECTED);
                });
    }

    @Override
    public Pointer getMonitorPointer() {
        return this.monitorPointer;
    }

}
