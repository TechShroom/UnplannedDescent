package com.techshroom.unplanned.window;

import org.lwjgl.glfw.GLFW;

import com.google.auto.service.AutoService;
import com.techshroom.unplanned.core.util.GLFWUtil;
import com.techshroom.unplanned.monitor.Monitor;
import com.techshroom.unplanned.pointer.PointerImpl;
import com.techshroom.unplanned.value.Dimension;

@AutoService(WindowGenerator.class)
public class GLFWWindowGenerator implements WindowGenerator {

    static {
        GLFWUtil.ensureInitialized();
    }

    @Override
    public Window generateWindow(Dimension size, String title, Monitor monitor,
            Window share) {
        return new GLFWWindow(PointerImpl.wrap((GLFW.glfwCreateWindow(size
                .getWidth(), size.getHeight(), title, monitor
                .getMonitorPointer().getPointer(), share.getWindowPointer()
                .getPointer()))));
    }

}
