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
package com.techshroom.unplanned.blitter;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.techshroom.unplanned.blitter.binding.BindableDrawableBase;
import com.techshroom.unplanned.core.util.GLErrorCheck;
import com.techshroom.unplanned.core.util.LifecycleObject;

// in this class normal=<q,r,s>
/**
 * VAO for storing everything. Stores all data in one VBO, with a layout of
 * {@code [XYZUVQRS, XYZUVQRS, ...]}.
 */
public class VAO extends BindableDrawableBase implements LifecycleObject {

    private static final int POS_INDEX = 0;
    private static final int TEX_INDEX = 1;
    private static final int NOR_INDEX = 2;
    private static final int POS_SIZE = 3;
    private static final int TEX_SIZE = 2;
    private static final int NOR_SIZE = 3;
    private static final int TOTAL_SIZE = POS_SIZE + TEX_SIZE + NOR_SIZE;
    private static final int[] EMPTY_INT = {};

    public static VAO create(int indicesVbo, int indiciesLength, List<Vertex> vertices) {
        return new VAO(makePrimitives(vertices), indicesVbo, indiciesLength);
    }

    public static VAO create(int[] indices, List<Vertex> vertices) {
        return new VAO(makePrimitives(vertices), indices);
    }

    private static FloatBuffer makePrimitives(List<Vertex> vertices) {
        int size = vertices.size();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(size * (POS_SIZE + TEX_SIZE + NOR_SIZE));
        for (Vertex v : vertices) {
            Vector3f pos = v.getPosition().toFloat();
            Vector2f tex = v.getTexture().toFloat();
            Vector3f nor = v.getNormal().toFloat();
            buffer.put(pos.toArray());
            buffer.put(tex.toArray());
            buffer.put(nor.toArray());
        }
        buffer.flip();
        return buffer;
    }

    private final FloatBuffer primitive;
    private final int[] indices;
    private final int indicesLength;
    private int vaoIndex;
    private int vboIndex;
    private int indicesVboIndex;

    private VAO(FloatBuffer primitive, int[] indices) {
        this.primitive = primitive;
        this.indices = indices;
        this.indicesLength = indices.length;
    }

    private VAO(FloatBuffer primitive, int indicesVbo, int indicesLength) {
        this.primitive = primitive;
        this.indices = EMPTY_INT;
        this.indicesVboIndex = indicesVbo;
        this.indicesLength = indicesLength;
    }

    @Override
    public void initialize() {
        GLErrorCheck.preflightCheck();

        vaoIndex = glGenVertexArrays();
        glBindVertexArray(vaoIndex);

        GLErrorCheck.check();

        // gen buffer
        vboIndex = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboIndex);

        GLErrorCheck.check();

        // pass in data
        glBufferData(GL_ARRAY_BUFFER, primitive, GL_STATIC_DRAW);

        GLErrorCheck.check();

        // enable 0/1/2, as we always use them
        glEnableVertexAttribArray(POS_INDEX);
        glEnableVertexAttribArray(TEX_INDEX);
        glEnableVertexAttribArray(NOR_INDEX);

        GLErrorCheck.check();

        final int stride = TOTAL_SIZE * Float.BYTES;
        // bind to VAO
        // first one is POSITION
        // - size 3 (x,y,z)
        // - type float
        // - don't bother normalizing (expensive + should already be handled!)
        // - stride 8 (x,y,z,u,v,q,r,s)
        // - pointer 0 (starts at index 0)
        glVertexAttribPointer(POS_INDEX, 3, GL_FLOAT, false, stride, 0);
        // second one is TEXTURE
        // - size 2 (u,v)
        // - type float
        // - don't bother normalizing (expensive + should already be handled!)
        // - stride 8 (x,y,z,u,v,q,r,s)
        // - pointer 3 (starts at index 3, skipping (0=x,1=y,2=z))
        glVertexAttribPointer(TEX_INDEX, 2, GL_FLOAT, false, stride, (POS_SIZE) * Float.BYTES);
        // third one is NORMAL
        // - size 3 (xN,yN,zN)
        // - type float
        // - don't bother normalizing (expensive + should already be handled!)
        // - stride 8 (x,y,z,u,v,q,r,s)
        // - pointer 5 (starts at index 5, skipping (0=x,1=y,2=z,3=u,4=t))
        glVertexAttribPointer(NOR_INDEX, 3, GL_FLOAT, false, stride, (POS_SIZE + TEX_SIZE) * Float.BYTES);

        GLErrorCheck.check();

        // setup indices
        if (indicesVboIndex == 0) {
            indicesVboIndex = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVboIndex);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        // this is kept in the VAO state, so we don't need to bind every time
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVboIndex);

        // unbind VAO/VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        GLErrorCheck.check();
    }

    @Override
    protected void doBind() {
        glBindVertexArray(vaoIndex);
    }

    @Override
    protected void doUnbind() {
        glBindVertexArray(0);
    }

    @Override
    protected void doDraw() {
        glDrawElements(GL_TRIANGLES, indicesLength, GL_UNSIGNED_INT, 0);
    }

    @Override
    public void destroy() {
        glDeleteVertexArrays(vaoIndex);
        glDeleteBuffers(vboIndex);
    }

}
