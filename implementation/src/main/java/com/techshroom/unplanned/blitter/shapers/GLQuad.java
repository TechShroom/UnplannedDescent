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

package com.techshroom.unplanned.blitter.shapers;

import java.util.List;

import com.techshroom.unplanned.blitter.Shape;
import com.techshroom.unplanned.blitter.Vertex;

public class GLQuad extends ShaperBase implements Quad {

    // 01
    // 23
    private static final int[] INDICES = { 0, 2, 3, 0, 1, 3 };

    private static int indiciesVbo;

    public GLQuad() {
        super(4);
    }

    @Override
    public Shape shapeWithNormals(List<Vertex> vertices) {
        verifyVertices(vertices);
        indiciesVbo = generateVbo(indiciesVbo, INDICES);
        return new VAOShape(vertices, indiciesVbo, INDICES.length);
    }

}
