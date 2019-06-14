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

import java.util.Arrays;
import java.util.List;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import com.techshroom.unplanned.blitter.Shape;
import com.techshroom.unplanned.blitter.Vertex;

/**
 * Shapes a rectangular prism, requires 8 points. Normals will be calculated. If
 * you choose to use textures, you may want to use the alternate form which
 * takes 24 texture points, enough to configure each face.
 * 
 * <p>
 * Prisms must have vertex data like this: <br>
 * Front:
 * 
 * <pre>
 * 1------4
 * |......|
 * |......|
 * |......|
 * 2------3
 * </pre>
 * 
 * Back:
 * 
 * <pre>
 * 5------8
 * |......|
 * |......|
 * |......|
 * 6------7
 * </pre>
 * 
 * Looks like this in 3D:
 * 
 * <pre>
 *    5--------8
 *   /        /|
 *  /        / |
 * 1--------4  |
 * |        |  |
 * |  6     |  7 -- (the 6 is hidden by the front faces)
 * |        | /
 * |        |/
 * 2--------3
 * </pre>
 * 
 * In list form:
 * 
 * <pre>
 * |  i  |  x  |  y  |  z  |
 * |  1  | -1  |  1  |  1  |
 * |  2  | -1  | -1  |  1  |
 * |  3  |  1  | -1  |  1  |
 * |  4  |  1  |  1  |  1  |
 * |  5  | -1  |  1  | -1  |
 * |  6  | -1  | -1  | -1  |
 * |  7  |  1  | -1  | -1  |
 * |  8  |  1  |  1  | -1  |
 * </pre>
 * </p>
 */
public interface RectangularPrism extends VertexShaper {

    default Shape shape(Vector3d a, Vector3d b, Vector2d... texture) {
        return shape(a, b, Arrays.asList(texture));
    }

    default Shape shape(Vector3d a, Vector3d b, List<Vector2d> texture) {
        List<Vertex> reshape = RPReshaper.reshape(a, b);
        return shapeWithNormals(RPReshaper.addNormals(reshape, texture));
    }

    @Override
    default Shape shape(List<Vertex> vertices) {
        // re-align 8 vertices as 24 with normals, call normal version
        return shapeWithNormals(RPReshaper.addNormals(vertices, null));
    }

    @Override
    default int getSize() {
        return 8;
    }

}
