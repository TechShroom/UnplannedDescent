package com.techshroom.unplanned.core.util;

import static org.lwjgl.glfw.GLFW.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;

public enum GLFWErrorHandler implements GLFWErrorCallback.SAM {

    HANDLER;

    private static boolean isSet = false;

    public static GLFWErrorHandler setAsErrorCallback() {
        if (!isSet) {
            isSet = true;
            glfwSetErrorCallback(GLFWErrorCallback(HANDLER));
        }
        return HANDLER;
    }

    private final GLFWErrorCallback delegate = Callbacks.errorCallbackPrint();

    @Override
    public void invoke(int error, long description) {
        this.delegate.invoke(error, description);
    }

}
