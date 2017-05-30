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
package com.techshroom.unplanned.blitter;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUseProgram;

import org.lwjgl.opengl.GL;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.matrix.GLMatrixUploader;
import com.techshroom.unplanned.blitter.matrix.MatrixUploader;
import com.techshroom.unplanned.blitter.shapers.GLShapes;
import com.techshroom.unplanned.blitter.shapers.Shapes;
import com.techshroom.unplanned.blitter.textures.GLTextureProvider;
import com.techshroom.unplanned.blitter.textures.TextureProvider;
import com.techshroom.unplanned.event.Event;
import com.techshroom.unplanned.event.window.WindowResizeEvent;
import com.techshroom.unplanned.window.ShaderInitialization;
import com.techshroom.unplanned.window.ShaderInitialization.Uniform;
import com.techshroom.unplanned.window.Window;

public class GLGraphicsContext implements GraphicsContext {

    private final TextureProvider textureProvider = new GLTextureProvider();
    private final Shapes shapes = new GLShapes();
    private final MatrixUploader matUpload = new GLMatrixUploader();

    private final Window window;

    public GLGraphicsContext(Window window) {
        this.window = window;
    }

    @Override
    public void clearGraphicsState() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        // choose our program again
        glUseProgram(ShaderInitialization.getProgram());
    }

    @Override
    public void makeActiveContext() {
        glfwMakeContextCurrent(window.getWindowPointer());
        GL.createCapabilities();

        // setup GL context
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        ShaderInitialization.setupShaders();
        glClearColor(0, 0, 0, 1);
        setLight(Vector3d.ZERO, Vector3d.ONE);

        Event.BUS.register(this);
        Vector2i size = window.getSize();
        onResize(WindowResizeEvent.create(window, size.getX(), size.getY()));
    }

    @Subscribe
    public void onResize(WindowResizeEvent event) {
        if (event.getSource() != window || glfwGetCurrentContext() != window.getWindowPointer()) {
            return;
        }
        Vector2i size = event.getSize();
        glViewport(0, 0, size.getX(), size.getY());
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(window.getWindowPointer());
    }

    @Override
    public void setLight(Vector3d pos, Vector3d color) {
        setVec3(Uniform.LIGHT_POSITION, pos);
        setVec3(Uniform.LIGHT_COLOR, color);
    }

    private void setVec3(Uniform uniform, Vector3d vec) {
        glUniform3f(ShaderInitialization.getUniform(uniform), (float) vec.getX(), (float) vec.getY(), (float) vec.getZ());
    }

    @Override
    public TextureProvider getTextureProvider() {
        return textureProvider;
    }

    @Override
    public Shapes getShapes() {
        return shapes;
    }

    @Override
    public MatrixUploader getMatrixUploader() {
        return matUpload;
    }

}
