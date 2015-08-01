package com.techshroom.unplanned.window;

import static com.google.common.base.Preconditions.checkState;
import static org.lwjgl.glfw.GLFW.GLFWFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.GLFWWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.GLFWWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.GLFWWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.GLFWWindowPosCallback;
import static org.lwjgl.glfw.GLFW.GLFWWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.GLFWWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetClipboardString;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwGetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWaitEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import org.lwjgl.Pointer;
import org.lwjgl.opengl.GL11;

import com.techshroom.unplanned.core.util.GLFWUtil;
import com.techshroom.unplanned.monitor.GLFWMonitor;
import com.techshroom.unplanned.monitor.Monitor;
import com.techshroom.unplanned.value.Dimension;
import com.techshroom.unplanned.value.Point;

public class GLFWWindow implements Window {

    static {
        GLFWUtil.ensureInitialized();
    }

    private final Pointer windowPtr;
    /**
     * When someone access the raw pointer, our data may be out of date. Attempt
     * to recover.
     */
    private boolean nonCallbackDataInvalid = false;
    private String title;
    private boolean visible;
    private boolean vsync;
    private Dimension size;
    private Dimension framebufferSize;
    private Point location;
    private Point cursorLocation;

    GLFWWindow(Pointer windowPtr) {
        this.windowPtr = windowPtr;
    }

    private void refreshData() {
        if (!this.nonCallbackDataInvalid) {
            return;
        }
        this.visible = getAttribute(GLFW_VISIBLE) == GL11.GL_TRUE;
    }

    @Override
    public Dimension getSize() {
        return this.size;
    }

    @Override
    public Dimension getFramebufferSize() {
        return this.framebufferSize;
    }

    @Override
    public Point getLocation() {
        return this.location;
    }

    @Override
    public Point getCursorLocation() {
        return this.cursorLocation;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public Monitor getMonitor() {
        return GLFWMonitor.getMonitor(glfwGetWindowMonitor(this.windowPtr
                .getPointer()));
    }

    @Override
    public String getClipboardContents() {
        return glfwGetClipboardString(this.windowPtr.getPointer());
    }

    @Override
    public boolean isVsyncOn() {
        return this.vsync;
    }

    @Override
    public boolean isCloseRequested() {
        return glfwWindowShouldClose(this.windowPtr.getPointer()) == GL11.GL_TRUE;
    }

    @Override
    public boolean isVisible() {
        refreshData();
        return this.visible;
    }

    @Override
    public int getAttribute(int attr) {
        return glfwGetWindowAttrib(this.windowPtr.getPointer(), attr);
    }

    @Override
    public Pointer getWindowPointer() {
        this.nonCallbackDataInvalid = true;
        return this.windowPtr;
    }

    @Override
    public void setSize(Dimension size) {
        glfwSetWindowSize(this.windowPtr.getPointer(), size.getWidth(),
                size.getHeight());
    }

    @Override
    public void setVsyncOn(boolean on) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCloseRequested(boolean requested) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMinimized(boolean minimized) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setVisible(boolean visible) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(this.windowPtr.getPointer(), title);
    }

    @Override
    public void destroy() {
        glfwDestroyWindow(this.windowPtr.getPointer());
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(this.windowPtr.getPointer());
    }

    @Override
    public void processEvents() {
        glfwPollEvents();
    }

    @Override
    public void waitForEvents() {
        glfwWaitEvents();
    }

    private void checkWindow(long window) {
        checkState(window == this.windowPtr.getPointer(), "incorrect window");
    }

    @Override
    public void onClose(OnCloseCallback callback) {
        glfwSetWindowCloseCallback(this.windowPtr.getPointer(),
                GLFWWindowCloseCallback(window -> {
                    checkWindow(window);
                    callback.onWindowClose(this);
                }));
    }

    @Override
    public void onMove(OnMoveCallback callback) {
        glfwSetWindowPosCallback(this.windowPtr.getPointer(),
                GLFWWindowPosCallback((window, x, y) -> {
                    checkWindow(window);
                    callback.onWindowMove(this, x, y);
                }));
    }

    @Override
    public void onResize(OnResizeCallback callback) {
        glfwSetWindowSizeCallback(this.windowPtr.getPointer(),
                GLFWWindowSizeCallback((window, width, height) -> {
                    checkWindow(window);
                    callback.onWindowResize(this, width, height);
                }));
    }

    @Override
    public void onResizeFramebuffer(OnResizeFramebufferCallback callback) {
        glfwSetFramebufferSizeCallback(this.windowPtr.getPointer(),
                GLFWFramebufferSizeCallback((window, width, height) -> {
                    checkWindow(window);
                    callback.onWindowFramebufferResize(this, width, height);
                }));
    }

    @Override
    public void onFocusChange(OnFocusCallback callback) {
        glfwSetWindowFocusCallback(
                this.windowPtr.getPointer(),
                GLFWWindowFocusCallback((window, focused) -> {
                    checkWindow(window);
                    callback.onWindowFocusChange(this, focused == GL11.GL_TRUE);
                }));
    }

    @Override
    public void onMinimizeChange(OnMinimizeChangeCallback callback) {
        glfwSetWindowIconifyCallback(this.windowPtr.getPointer(),
                GLFWWindowIconifyCallback((window, minimized) -> {
                    checkWindow(window);
                    callback.onWindowMinimizeChange(this,
                            minimized == GL11.GL_TRUE);
                }));
    }

    @Override
    public void onRefreshRequested(OnRefreshRequestedCallback callback) {
        glfwSetWindowRefreshCallback(this.windowPtr.getPointer(),
                GLFWWindowRefreshCallback((window) -> {
                    checkWindow(window);
                    callback.onWindowRefreshRequested(this);
                }));
    }

}
