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

import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.techshroom.unplanned.core.util.GLErrorCheck;

/**
 * Handles GL shader creation and setup.
 */
public class ShaderInitialization {

    private static int vertexShaderId;
    private static int fragShaderId;
    private static int programId;
    
    private static int matrixUniform;

    private static void checkInit() {
        checkState(programId != 0, "setupShaders not called yet");
    }

    public static int getProgram() {
        checkInit();
        return programId;
    }
    
    public static int getMatrixUniform() {
        checkInit();
        return matrixUniform;
    }

    public static void setupShaders() {
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

    private static void initIds() {
        vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        fragShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        programId = glCreateProgram();
    }

    private static void compileShaders() throws IOException {
        String vertexShader = loadFile("/com/techshroom/unplanned/shaders/vertex.glsl");
        String fragmentShader = loadFile("/com/techshroom/unplanned/shaders/fragment.glsl");

        compileShader(vertexShaderId, vertexShader);
        compileShader(fragShaderId, fragmentShader);
    }

    private static void compileShader(int shader, String data) {
        glShaderSource(shader, data);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Shader failed to compile: " + glGetShaderInfoLog(shader));
        }
    }

    private static void linkShaders() {
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
    
    private static void setBindings() {
        matrixUniform = glGetUniformLocation(programId, "matrix");
    }

    private static String loadFile(String file) throws IOException {
        return Resources.toString(ShaderInitialization.class.getResource(file), StandardCharsets.UTF_8);
    }

}
