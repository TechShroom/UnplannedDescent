package com.techshroom.unplanned.core.util;

import static org.lwjgl.glfw.GLFW.glfwInit;

public final class GLFWUtil {

    public static void ensureInitialized() {
        glfwInit();
        GLFWErrorHandler.setAsErrorCallback();
    }

    private GLFWUtil() {
    }

}
