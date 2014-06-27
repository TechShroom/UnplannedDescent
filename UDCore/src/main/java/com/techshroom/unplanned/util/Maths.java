package com.techshroom.unplanned.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class Maths {

	public static final double med = calculateMachineEpsilonDouble();

	static {
		System.err.println("Got MED " + med);
		System.err.println("Java reports ulp for 1.0 is " + Math.ulp(1.0));
	}

	private static double calculateMachineEpsilonDouble() {
		double machEps = 1.0d;

		do
			machEps /= 2.0d;
		while ((double) (1.0 + (machEps / 2.0)) != 1.0);

		return machEps;
	}

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
			double x = r.getX(), y = r.getY(), w = r.getWidth(), h = r
					.getHeight();
			// clear rect
			r.setRect(0, 0, 0, 0);
			// get points
			Point2D[] points = new Point2D[] { new Point2D.Double(x, y),
					new Point2D.Double(w + x, h + y) };
			// calc cos/sin
			double s = TrigTableLookup.sin(theta), c = TrigTableLookup
					.cos(theta);
			// expand rect to fit
			for (Point2D p : points) {
				p.setLocation((p.getX() * c) - (p.getY() * s), (p.getX() * s)
						+ (p.getY() * c));
			}
			r.setRect(points[0].getX(), points[0].getY(), points[1].getX()
					- points[0].getX(), points[1].getY() - points[0].getY());
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
			System.out.println(dp);
			return dp * Math.cos(Math.toRadians(thetaSurface));
		} else {
			System.out.println(dp);
			return dp * Math.sin(Math.toRadians(thetaSurface));
		}

	}

	public static double projectLineAlongSurfaceXY(double xSurface,
			double ySurface, double xLine, double yLine, boolean DoY) {
		double dp = dotProduct(xLine, yLine,
				Maths.normalizeX(xSurface, ySurface),
				Maths.normalizeY(xSurface, ySurface));
		if (!DoY) {
			System.out.println(dp);
			return dp * xSurface;
		} else {
			System.out.println(dp);
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

	public static double dotProduct(double ax, double ay, double bx, double by) {
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

	public static final class TrigTableLookup {
		// Set to Math.PI*2 if you want radians, or 360d for degrees
		private static final double circleSize = 360d;
		private static final double doubleCircleSize = circleSize * 2;
		private static final int virtualSize = 1048576;
		private static final double conversionFactor = (virtualSize / circleSize);
		private static final double tanLeadingCoefficient = (-circleSize
				/ Math.PI * 2);
		private static final double[] SinTable = new double[16384];

		static {
			for (int i = 0; i < 16384; i++) {
				SinTable[i] = Math.sin(i * Math.PI * 2 / 16384);
			}
		}

		private static double lookup(int val) {
			int index = val / 64;
			double LUTSinA = SinTable[index & 16383];
			double LUTSinB = SinTable[(index + 1) & 16383];
			double LUTSinW = (val & 63) / 63d;
			return LUTSinW * (LUTSinB - LUTSinA) + LUTSinA;
		}

		public static double sin(double Value) {
			return lookup((int) (Value * conversionFactor));
		}

		public static double cos(double Value) {
			return lookup((int) (Value * conversionFactor) + 262144);
		}

		public static double tan(double Value) {
			int k = (int) (Value * conversionFactor);
			// Central 5000 values use
			// the 1/x form
			int wrapped = (((k + 2000) << 13) >> 13);
			if (wrapped < -258144) { // 262144-5000
				return tanLeadingCoefficient
						/ ((4 * Value) % (doubleCircleSize) - circleSize);
			}
			return lookup(k) / lookup(k + 262144);

		}
	}

	public static boolean isPowerOfTwo(int x) {
		return x != 0 && (x & (x - 1)) == 0;
	};
}
