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

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.opengl.GL;

import com.techshroom.unplanned.blitter.matrix.GLMatrixUploader;
import com.techshroom.unplanned.blitter.matrix.MatrixUploader;
import com.techshroom.unplanned.blitter.shapers.GLShapes;
import com.techshroom.unplanned.blitter.shapers.Shapes;
import com.techshroom.unplanned.blitter.textures.GLTextureProvider;
import com.techshroom.unplanned.blitter.textures.TextureProvider;
import com.techshroom.unplanned.window.ShaderInitialization;

public class GLGraphicsContext implements GraphicsContext {

    private final TextureProvider textureProvider = new GLTextureProvider();
    private final Shapes shapes = new GLShapes();
    private final MatrixUploader matUpload = new GLMatrixUploader();

    private final long window;

    public GLGraphicsContext(long window) {
        this.window = window;
    }

    @Override
    public void clearGraphicsState() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void makeActiveContext() {
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // setup GL context
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        ShaderInitialization.setupShaders();
        glClearColor(1, 0, 1, 1);
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(window);
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
