package com.techshroom.unplanned.monitor;

import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;

import java.util.List;

import org.lwjgl.PointerBuffer;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.core.util.GLFWUtil;

@AutoService(MonitorProvider.class)
public class GLFWMonitorProvider implements MonitorProvider {

    static {
        GLFWUtil.ensureInitialized();
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
