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

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Vertex {

    public static Builder at(double x, double y, double z) {
        return builder().position(x, y, z);
    }

    public static Builder at(Vector3d pos) {
        return builder().position(pos);
    }

    public static Builder builder() {
        return new AutoValue_Vertex.Builder().texture(Vector2d.ZERO).normal(Vector3d.ZERO);
    }

    @AutoValue.Builder
    public interface Builder {

        default Builder position(double x, double y, double z) {
            return position(new Vector3d(x, y, z));
        }

        Builder position(Vector3d pos);

        default Builder texture(double u, double v) {
            return texture(new Vector2d(u, v));
        }

        Builder texture(Vector2d tex);

        default Builder normal(double x, double y, double z) {
            return normal(new Vector3d(x, y, z));
        }

        Builder normal(Vector3d pos);

        Vertex build();

    }

    public abstract Vector3d getPosition();

    public abstract Vector2d getTexture();

    public abstract Vector3d getNormal();

    public abstract Builder toBuilder();

}
