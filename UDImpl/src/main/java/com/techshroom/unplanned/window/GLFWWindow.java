package com.techshroom.unplanned.window;

import static com.google.common.base.Preconditions.checkState;
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

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;

import com.techshroom.unplanned.core.util.GLFWUtil;
import com.techshroom.unplanned.monitor.GLFWMonitor;
import com.techshroom.unplanned.monitor.Monitor;
import com.techshroom.unplanned.pointer.Pointer;
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
        return GLFWMonitor
                .getMonitor(glfwGetWindowMonitor(this.windowPtr.address()));
    }

    @Override
    public String getClipboardContents() {
        return glfwGetClipboardString(this.windowPtr.address());
    }

    @Override
    public boolean isVsyncOn() {
        return this.vsync;
    }

    @Override
    public boolean isCloseRequested() {
        return glfwWindowShouldClose(this.windowPtr.address());
    }

    @Override
    public boolean isVisible() {
        refreshData();
        return this.visible;
    }

    @Override
    public int getAttribute(int attr) {
        return glfwGetWindowAttrib(this.windowPtr.address(), attr);
    }

    @Override
    public Pointer getWindowPointer() {
        this.nonCallbackDataInvalid = true;
        return this.windowPtr;
    }

    @Override
    public void setSize(Dimension size) {
        glfwSetWindowSize(this.windowPtr.address(), size.getWidth(),
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
        glfwSetWindowTitle(this.windowPtr.address(), title);
    }

    @Override
    public void destroy() {
        glfwDestroyWindow(this.windowPtr.address());
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(this.windowPtr.address());
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
        checkState(window == this.windowPtr.address(), "incorrect window");
    }

    @Override
    public void onClose(OnCloseCallback callback) {
        glfwSetWindowCloseCallback(this.windowPtr.address(),
                GLFWWindowCloseCallback.create(window -> {
                    checkWindow(window);
                    callback.onWindowClose(this);
                }));
    }

    @Override
    public void onMove(OnMoveCallback callback) {
        glfwSetWindowPosCallback(this.windowPtr.address(),
                GLFWWindowPosCallback.create((window, x, y) -> {
                    checkWindow(window);
                    callback.onWindowMove(this, x, y);
                }));
    }

    @Override
    public void onResize(OnResizeCallback callback) {
        glfwSetWindowSizeCallback(this.windowPtr.address(),
                GLFWWindowSizeCallback.create((window, width, height) -> {
                    checkWindow(window);
                    callback.onWindowResize(this, width, height);
                }));
    }

    @Override
    public void onResizeFramebuffer(OnResizeFramebufferCallback callback) {
        glfwSetFramebufferSizeCallback(this.windowPtr.address(),
                GLFWFramebufferSizeCallback.create((window, width, height) -> {
                    checkWindow(window);
                    callback.onWindowFramebufferResize(this, width, height);
                }));
    }

    @Override
    public void onFocusChange(OnFocusCallback callback) {
        glfwSetWindowFocusCallback(this.windowPtr.address(),
                GLFWWindowFocusCallback.create((window, focused) -> {
                    checkWindow(window);
                    callback.onWindowFocusChange(this, focused);
                }));
    }

    @Override
    public void onMinimizeChange(OnMinimizeChangeCallback callback) {
        glfwSetWindowIconifyCallback(this.windowPtr.address(),
                GLFWWindowIconifyCallback.create((window, minimized) -> {
                    checkWindow(window);
                    callback.onWindowMinimizeChange(this,
                            minimized);
                }));
    }

    @Override
    public void onRefreshRequested(OnRefreshRequestedCallback callback) {
        glfwSetWindowRefreshCallback(this.windowPtr.address(),
                GLFWWindowRefreshCallback.create((window) -> {
                    checkWindow(window);
                    callback.onWindowRefreshRequested(this);
                }));
    }

}
