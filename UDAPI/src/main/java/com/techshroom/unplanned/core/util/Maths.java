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
package com.techshroom.unplanned.core.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.stream.IntStream;

import com.flowpowered.math.vector.Vector4i;

public final class Maths {

    /**
     * Provides geometry manipulation, such as axis-normalization and rectangle
     * rotations.
     * 
     * @author Kenzie Togami
     */
    public static final class Geometry {

        private Geometry() {
            throw new AssertionError("Don't create");
        }

        /**
         * Rotates the given rectangle by <code>theta</code> degrees, treating
         * it as if it were drawn at that angle and not axis-aligned.
         * 
         * @param r
         *            - the original rectangle
         * @param theta
         *            - the rotation angle
         * @return <code>r</code>, rotated by <code>theta</code>
         */
        public static Rectangle2D rotateRect(Rectangle2D r, double theta) {
            // store data
            double x = r.getX(), y = r.getY(), w = r.getWidth(),
                    h = r.getHeight();
            // clear rect
            r.setRect(0, 0, 0, 0);
            // get points
            Point2D[] points = new Point2D[] { new Point2D.Double(x, y),
                    new Point2D.Double(w + x, h + y) };
            // calc cos/sin
            double s = TrigTableLookup.sin(theta),
                    c = TrigTableLookup.cos(theta);
            // expand rect to fit
            for (Point2D p : points) {
                p.setLocation((p.getX() * c) - (p.getY() * s),
                        (p.getX() * s) + (p.getY() * c));
            }
            r.setRect(points[0].getX(), points[0].getY(),
                    points[1].getX() - points[0].getX(),
                    points[1].getY() - points[0].getY());
            return r;
        }

        public static double[] pointsAsDoubles(Point2D[] points) {
            double[] out = new double[points.length * 2];
            for (int i = 0; i < out.length; i += 2) {
                Point2D p = points[i / 2];
                out[i] = p.getX();
                out[i + 1] = p.getY();
            }
            return out;
        }

        public static Point2D[] doublesAsPoints(double[] points) {
            if (points.length % 2 != 0) {
                throw new IllegalArgumentException("need pairs of doubles");
            }
            Point2D[] temp = new Point2D[points.length / 2];
            for (int i = 0; i < points.length; i += 2) {
                temp[i / 2] = new Point2D.Double(points[i], points[i + 1]);
            }
            return temp;
        }
    }

    private Maths() {
        throw new AssertionError("Don't create");
    }

    /**
     * Finds the length of a line when projected along another line. Used for
     * collisions.
     * 
     * @param thetaSurface
     *            - The angle the surface to be projected upon makes with the
     *            horizontal
     * @param thetaLineToProject
     *            - The angle the line makes with the horizontal
     * @param magnitude
     *            - The length of the line.
     * @param getY
     *            - Gets the length of the line when projected along the line
     *            perpendicular to the thetaSurface given (eg, find the y).
     */
    public static double projectLineAlongSurface(double thetaSurface,
            double thetaLineToProject, double magnitude, boolean getY) {
        double dp = dotProductAngles(magnitude, thetaLineToProject, 1,
                thetaSurface);
        if (!getY) {
            return dp * Math.cos(Math.toRadians(thetaSurface));
        } else {
            return dp * Math.sin(Math.toRadians(thetaSurface));
        }

    }

    public static double projectLineAlongSurfaceXY(double xSurface,
            double ySurface, double xLine, double yLine, boolean DoY) {
        double dp =
                dotProduct(xLine, yLine, Maths.normalizeX(xSurface, ySurface),
                        Maths.normalizeY(xSurface, ySurface));
        if (!DoY) {
            return dp * xSurface;
        } else {
            return dp * ySurface;
        }

    }

    public static double normalizeX(double x, double y) {
        double len_v = Math.sqrt(x * x + y * y);
        return x / len_v;
    }

    public static double normalizeY(double x, double y) {
        double len_v = Math.sqrt(x * x + y * y);
        return y / len_v;
    }

    public static double dotProduct(double ax, double ay, double bx,
            double by) {
        return ax * ay + bx * by;
    }

    public static double dotProductAngles(double amag, double atheta,
            double bmag, double btheta) {
        double ax = Math.cos(Math.toRadians(atheta)) * amag;
        double bx = Math.cos(Math.toRadians(btheta)) * bmag;
        double ay = Math.sin(Math.toRadians(atheta)) * amag;
        double by = Math.sin(Math.toRadians(btheta)) * bmag;
        return ax * bx + ay * by;
    }

    /**
     * Perform linear interpolation on positions
     * 
     * @param pos1
     *            -The first position (does not matter x or y)
     * @param pos2
     *            -The second position
     * @param v
     *            -The interpolaty-bit. Decimal between 0 and 1.
     * @return -The position (actually an average of pos1/pos2 + pos1).
     */
    public static float lerp(float pos1, float pos2, float v) {
        return pos1 + (pos2 - pos1) * v;
    }

    public static boolean lessThanOrEqualTo(double a, double b) {
        int c = Double.compare(a, b);
        return c <= 0;
    }

    public static boolean lessThan(double a, double b) {
        int c = Double.compare(a, b);
        return c < 0;
    }

    public static boolean greaterThanOrEqualTo(double a, double b) {
        int c = Double.compare(a, b);
        return c >= 0;
    }

    public static boolean greaterThan(double a, double b) {
        int c = Double.compare(a, b);
        return c > 0;
    }

    /*
     * Disabled until matrix stuff is actually needed
     * 
     * public static enum Axis { X, Y, Z }
     * 
     * public static Matrix4f createRotMatrix(double theta, Axis axis) {
     * Matrix4f rmat = new Matrix4f(); // identity float c = (float)
     * qcos(theta); float s = (float) qsin(theta); switch (axis) { case X:
     * rmat.m11 = c; rmat.m22 = c; rmat.m21 = s; rmat.m12 = -s; break; case Y:
     * rmat.m00 = c; rmat.m22 = c; rmat.m02 = s; rmat.m20 = -s; break; case Z:
     * rmat.m00 = c; rmat.m11 = c; rmat.m10 = s; rmat.m01 = -s; break; default:
     * throw new IllegalArgumentException("Invalid Axis"); } return rmat; }
     * 
     * public static Matrix4f createTransMatrix(double ix, double iy, double iz)
     * { Matrix4f imat = new Matrix4f(); // identity imat.m03 = (float) ix;
     * imat.m13 = (float) iy; imat.m23 = (float) iz; return
     * imat.transpose(imat); }
     * 
     * public static Matrix4f createScaleMatrix(double sx, double sy, double sz)
     * { Matrix4f imat = new Matrix4f(); // identity imat.m00 = (float) sx;
     * imat.m11 = (float) sy; imat.m22 = (float) sz; return imat; }
     */

    public static double fastSin(double deg) {
        return TrigTableLookup.sin(deg);
    }

    public static double fastCos(double deg) {
        return TrigTableLookup.cos(deg);
    }

    public static double fastTan(double deg) {
        return TrigTableLookup.tan(deg);
    }

    private static final class TrigTableLookup {

        // Set to Math.PI*2 if you want radians, or 360d for degrees
        private static final double CIRCLE_SIZE = 360d;
        private static final double CIRCLE_SIZE_X2 = CIRCLE_SIZE * 2;
        private static final int VIRTUAL_SIZE = 1048576;
        private static final double CONVERSION_FACTOR =
                (VIRTUAL_SIZE / CIRCLE_SIZE);
        private static final double TAN_COEFF = -CIRCLE_SIZE / Math.PI * 2;
        private static final int TABLE_SIZE = 16384;
        private static final int TABLE_SIZE_MIN_1 = TABLE_SIZE - 1;
        private static final double[] SIN_TABLE = new double[TABLE_SIZE];

        static {
            for (int i = 0; i < 16384; i++) {
                SIN_TABLE[i] = Math.sin(i * Math.PI * 2 / TABLE_SIZE);
            }
        }

        private static double lookup(int val) {
            int index = val / 64;
            double LUTSinA = SIN_TABLE[index & TABLE_SIZE_MIN_1];
            double LUTSinB = SIN_TABLE[(index + 1) & TABLE_SIZE_MIN_1];
            double LUTSinW = (val & 63) / 63d;
            return LUTSinW * (LUTSinB - LUTSinA) + LUTSinA;
        }

        public static double sin(double angle) {
            return lookup((int) (angle * CONVERSION_FACTOR));
        }

        public static double cos(double angle) {
            return lookup((int) (angle * CONVERSION_FACTOR) + 262144);
        }

        public static double tan(double angle) {
            int k = (int) (angle * CONVERSION_FACTOR);
            // Central 5000 values use
            // the 1/x form
            int wrapped = (((k + 2000) << 13) >> 13);
            if (wrapped < -258144) { // 5000-262144
                return TAN_COEFF
                        / ((4 * angle) % (CIRCLE_SIZE_X2) - CIRCLE_SIZE);
            }
            return lookup(k) / lookup(k + 262144);

        }
    }

    public static int nextPowerOfTwo(int a) {
        checkArgument(a > 0, "a must be > 0");
        // cheap & fast ceil(log_2(a))
        int exp = 32 - Integer.numberOfLeadingZeros(a - 1);
        return 1 << exp;
    }

    public static boolean isPowerOfTwo(int x) {
        return x != 0 && (x & (x - 1)) == 0;
    }

    /**
     * Gets the smallest of all the given ints.
     * 
     * @return the smallest int from ints
     */
    public static int min(int first, int... rest) {
        return IntStream.concat(IntStream.of(first), IntStream.of(rest)).min()
                .getAsInt();
    }

    /**
     * Gets the largest of all the given ints.
     * 
     * @return the largest int from ints
     */
    public static int max(int first, int... rest) {
        return IntStream.concat(IntStream.of(first), IntStream.of(rest)).max()
                .getAsInt();
    }

    /**
     * Check for integer
     * 
     * @param test
     *            - the String to check for integer
     * @return if the String represents an integer
     */
    public static boolean isInt(String test) {
        try {
            Integer.parseInt(test);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("fallthrough")
    public static Vector4i getColorVector(String color) {
        String c;
        if (color.startsWith("#")) {
            c = color.substring(1);
        } else {
            c = color;
        }
        int r;
        int g;
        int b;
        int a = 255;
        try {
            switch (c.length()) {
                case 4:
                    // RGBA, RGB handled below
                    a = Integer.parseInt(c.substring(3, 4), 16) * 0x11;
                case 3:
                    // RGB
                    r = Integer.parseInt(c.substring(0, 1), 16) * 0x11;
                    g = Integer.parseInt(c.substring(1, 2), 16) * 0x11;
                    b = Integer.parseInt(c.substring(2, 3), 16) * 0x11;
                    break;
                case 8:
                    // RRGGBBAA, RRGGBB handled below
                    a = Integer.parseInt(c.substring(6, 8), 16);
                case 6:
                    // RRGGBB
                    r = Integer.parseInt(c.substring(0, 2), 16);
                    g = Integer.parseInt(c.substring(2, 4), 16);
                    b = Integer.parseInt(c.substring(4, 6), 16);
                    break;
                default:
                    throw new IllegalArgumentException("Not a hex color: " + color);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a hex color: " + color, e);
        }
        return new Vector4i(r, g, b, a);
    }

}
