package com.techshroom.unplanned.core.util;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;

import org.lwjgl.glfw.GLFWErrorCallback;

public enum GLFWErrorHandler implements GLFWErrorCallback.SAM {

    HANDLER;

    private static boolean isSet = false;

    public static GLFWErrorHandler setAsErrorCallback() {
        if (!isSet) {
            isSet = true;
            glfwSetErrorCallback(GLFWErrorCallback.create(HANDLER));
        }
        return HANDLER;
    }

    private final GLFWErrorCallback delegate = GLFWErrorCallback.createPrint();

    @Override
    public void invoke(int error, long description) {
        this.delegate.invoke(error, description);
    }

}
