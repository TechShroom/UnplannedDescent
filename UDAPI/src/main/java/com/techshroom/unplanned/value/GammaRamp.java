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
package com.techshroom.unplanned.value;

import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GammaRamp {

    public static final class Channel {

        public final int[] data;

        private Channel(int[] data) {
            this.data = data.clone();
        }

        @Override
        public String toString() {
            return Arrays.toString(this.data);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(data);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Channel other = (Channel) obj;
            if (!Arrays.equals(data, other.data))
                return false;
            return true;
        }

    }

    public static final GammaRamp of(int[] red, int[] green, int[] blue,
            int size) {
        checkState(red.length == size, "red array was not of size %s", size);
        checkState(green.length == size, "green array was not of size %s",
                size);
        checkState(blue.length == size, "blue array was not of size %s", size);
        return new AutoValue_GammaRamp(new Channel(red), new Channel(green),
                new Channel(blue), size);
    }

    public static final GammaRamp of(float exponent) {
        // adapted from GLFW source
        int[] values = new int[256];
        for (int i = 0; i < values.length; i++) {
            double value;

            // Calculate intensity
            value = i / 255.0;
            // Apply gamma curve
            value = Math.pow(value, 1.0 / exponent) * 65535.0 + 0.5;

            // Clamp to value range
            if (value > 65535.0) {
                value = 65535.0;
            }

            // unsigned short cast
            values[i] = ((int) value) & 0xFFFF;
        }
        return of(values, values, values, values.length);
    }

    public abstract Channel getRedChannel();

    public abstract Channel getGreenChannel();

    public abstract Channel getBlueChannel();

    public abstract int getSize();

}
