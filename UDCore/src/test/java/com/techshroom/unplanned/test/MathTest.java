package com.techshroom.unplanned.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.techshroom.unplanned.util.BetterArrays;
import com.techshroom.unplanned.util.Maths.TrigTableLookup;

public class MathTest {

	/**
	 * Maths can do trig.
	 */
	@Test
	public void doTrig() {
		System.err.println("expected delta " + getDelta());
		double delta = getDelta();
		for (int i = 0; i < 360 * 500; i++) {
			double deg = (i / 500);
			assertEquals("cos " + deg, Math.cos(Math.toRadians(deg)),
					TrigTableLookup.cos(deg), delta);
			assertEquals("sin " + deg, Math.sin(Math.toRadians(deg)),
					TrigTableLookup.sin(deg), delta);
			if (Double.compare(90d, deg) == 0 || Double.compare(270d, deg) == 0) {
				// rounding error for radians conversion, just make sure this is
				// -Infinity
				assertEquals("tan " + deg, Double.NEGATIVE_INFINITY,
						TrigTableLookup.tan(deg), delta);
				continue;
			}
			assertEquals("tan " + deg, Math.tan(Math.toRadians(deg)),
					TrigTableLookup.tan(deg), delta);
		}
	}

	@SuppressWarnings("unused")
	public double getDelta() {
		double inc = 0.000001;
		List<Double> delts = Arrays.asList(BetterArrays
				.<Double[]> createAndFill(Double.class, 360 * 500, Double.NaN));
		List<Double> deltc = Arrays.asList(BetterArrays
				.<Double[]> createAndFill(Double.class, 360 * 500, Double.NaN));
		List<Double> deltt = Arrays.asList(BetterArrays
				.<Double[]> createAndFill(Double.class, 360 * 500, Double.NaN));
		double delta = 0.0000000001;
		for (int i = 0; i < 360 * 500; i++) {
			int tests = 1 | 2 | 4;
			double deg = (i / 500d);
			while (tests != 0 && delta < 0.1) {
				double math = 0.0, maths = 0.0;
				if ((tests & 1) == 1) {
					math = Math.cos(Math.toRadians(deg));
					maths = TrigTableLookup.cos(deg);
					if (doubleIsDifferent(math, maths)) {
						assertEquals(Double.NaN, deltc.get(i), 0);
						deltc.set(i, delta);
						tests &= ~1;
					}
				}
				if ((tests & 2) == 2) {
					math = Math.sin(Math.toRadians(deg));
					maths = TrigTableLookup.sin(deg);
					if (doubleIsDifferent(math, maths)) {
						assertEquals(Double.NaN, delts.get(i), 0);
						delts.set(i, delta);
						tests &= ~2;
					}
				}
				if ((tests & 4) == 4) {
					math = Math.tan(Math.toRadians(deg));
					maths = TrigTableLookup.tan(deg);
					if (doubleIsDifferent(math, maths)) {
						assertEquals(Double.NaN, deltt.get(i), 0);
						deltt.set(i, delta);
						tests &= ~4;
					}
				}
				delta += inc;
			}
			if ((tests & 1) == 1) {
				deltc.set(i, delta);
			}
			if ((tests & 2) == 2) {
				delts.set(i, delta);
			}
			if ((tests & 4) == 4) {
				deltt.set(i, delta);
			}
		}
		double rs = 0, rc = 0, rt = 0;
		double ms = Double.MIN_NORMAL, mc = Double.MIN_NORMAL, mt = Double.MIN_NORMAL;
		double mis = Double.MAX_VALUE, mic = Double.MAX_VALUE, mit = Double.MAX_VALUE;
		double s = delts.size();
		for (int i = 0; i < s; i++) {
			rs += (delts.get(i) / s);
			rc += (deltc.get(i) / s);
			rt += (deltt.get(i) / s);

			ms = Math.max(ms, delts.get(i));
			mc = Math.max(mc, deltc.get(i));
			mt = Math.max(mt, deltt.get(i));

			mis = Math.min(mis, delts.get(i));
			mic = Math.min(mic, deltc.get(i));
			mit = Math.min(mit, deltt.get(i));
		}
		return (ms + mc + mt) / 3;
	}

	static private boolean doubleIsDifferent(double d1, double d2) {
		if (Double.compare(d1, d2) == 0) {
			return false;
		}

		return true;
	}
}
