/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
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
package com.techshroom.unplanned.blitter.shapers;

import static com.google.common.base.Preconditions.checkArgument;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.util.List;

import com.techshroom.unplanned.blitter.Vertex;

public abstract class ShaperBase implements VertexShaper {

    protected static int generateVbo(int indiciesVbo, int[] indicies) {
        if (indiciesVbo == 0) {
            indiciesVbo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indiciesVbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STATIC_DRAW);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }
        return indiciesVbo;
    }

    private final int vertexCount;

    protected ShaperBase(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    protected void verifyVertices(List<Vertex> vertices) {
        checkArgument(vertices.size() == vertexCount, "exactly %s vertices are required, recieved %s", vertexCount, vertices.size());
    }

}
