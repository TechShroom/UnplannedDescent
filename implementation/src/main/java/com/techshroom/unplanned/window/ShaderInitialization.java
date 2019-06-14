/*
 * This file is part of unplanned-descent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshroom.com>
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

package com.techshroom.unplanned.window;

import static com.google.common.base.Preconditions.checkState;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.techshroom.unplanned.core.util.GLErrorCheck;

/**
 * Handles GL shader creation and setup.
 */
public class ShaderInitialization {

    public enum Uniform {
        MVP("mvpMat"), MODEL("modelMat"), NORMAL("normalMat"), LIGHT_POSITION("lightPos"), LIGHT_COLOR("lightColor");

        private final String shaderName;

        Uniform(String shaderName) {
            this.shaderName = shaderName;
        }
    }

    private int vertexShaderId;
    private int fragShaderId;
    private int programId;

    private final Map<Uniform, Integer> uniforms = new EnumMap<>(Uniform.class);

    private void checkInit() {
        checkState(programId != 0, "setupShaders not called yet");
    }

    public int getProgram() {
        checkInit();
        return programId;
    }

    public int getUniform(Uniform uniform) {
        checkInit();
        Integer id = uniforms.get(uniform);
        checkState(id != null, "missing uniform %s", uniform);
        return id;
    }

    public void setupShaders() {
        if (programId != 0) {
            return;
        }
        GLErrorCheck.preflightCheck();
        try {
            initIds();
            compileShaders();
            linkShaders();
            setBindings();
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
    }

    private void initIds() {
        vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        fragShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        programId = glCreateProgram();
    }

    private void compileShaders() throws IOException {
        String vertexShader = loadFile("/com/techshroom/unplanned/shaders/vertex.glsl");
        String fragmentShader = loadFile("/com/techshroom/unplanned/shaders/fragment.glsl");

        compileShader("vertex", vertexShaderId, vertexShader);
        compileShader("fragment", fragShaderId, fragmentShader);
    }

    private void compileShader(String label, int shader, String data) {
        glShaderSource(shader, data);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new IllegalStateException(label + " shader failed to compile: " + glGetShaderInfoLog(shader));
        }
    }

    private void linkShaders() {
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragShaderId);
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Program failed to link: " + glGetProgramInfoLog(programId));
        }

        /* detach and delete as recommended by the Internet */
        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, fragShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragShaderId);

        glUseProgram(programId);

        int texSampleLoc = glGetUniformLocation(programId, "texSample");
        glUniform1i(texSampleLoc, 0);

        GLErrorCheck.check();
    }

    private void setBindings() {
        for (Uniform u : Uniform.values()) {
            uniforms.put(u, glGetUniformLocation(programId, u.shaderName));
        }
    }

    private static String loadFile(String file) throws IOException {
        return Resources.toString(ShaderInitialization.class.getResource(file), StandardCharsets.UTF_8);
    }

}
