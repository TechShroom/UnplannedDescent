package com.techshroom.unplanned.test;

import static org.junit.Assert.assertArrayEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.techshroom.unplanned.modloader.BetterArrays;

public class BATest {

	private static Object[] GENERIC_array, GENERIC_reverse;
	private static int[] NOTGENERIC_arrayi, NOTGENERIC_reversei;
	private static Object[] FILL_notfilled, FILL_filled;
	private static Object[] SPL_original, SPL_normalcp, SPL_reversecp,
			SPL_bigstepcp, SPL_rbigstepcp;

	@BeforeClass
	public static void setUp() throws Exception {
		Object obj = new Object();
		GENERIC_array = new Object[] { "abc", 1, 2, 3, obj };
		GENERIC_reverse = new Object[] { obj, 3, 2, 1, "abc" };
		NOTGENERIC_arrayi = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		NOTGENERIC_reversei = new int[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
		FILL_notfilled = new Object[10];
		FILL_filled = new Object[] { 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
				'a', 'a' };
		SPL_original = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		SPL_normalcp = new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		SPL_reversecp = new Object[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
		SPL_bigstepcp = new Object[] { 0, 2, 4, 6, 8 };
		SPL_rbigstepcp = new Object[] { 9, 7, 5, 3, 1 };
	}

	/**
	 * BetterArrays can reverse an array.
	 */
	@Test
	public void reversesArray() {
		Object[] reversed_ba = BetterArrays.reverse(GENERIC_array);
		assertArrayEquals(GENERIC_reverse, reversed_ba);
	}

	/**
	 * BetterArrays can reverse a non-generic array.
	 */
	@Test
	public void reversesNonGenericArray() {
		int[] reversed_ba = BetterArrays.reverseNonGeneric(NOTGENERIC_arrayi);
		assertArrayEquals(NOTGENERIC_reversei, reversed_ba);
	}

	/**
	 * BetterArrays can fill an array.
	 */
	@Test
	public void fillsArray() {
		BetterArrays.fillArray(FILL_notfilled, 'a');
		assertArrayEquals(FILL_filled, FILL_notfilled);
	}

	/**
	 * BetterArrays can create and fill an array.
	 */
	@Test
	public void createAndFillsArray() {
		Object[] wascreated = BetterArrays.createAndFill(Object.class, 10, 'a');
		assertArrayEquals(FILL_filled, wascreated);
	}

	/**
	 * BetterArrays can splice an array.
	 */
	@Test
	public void splicesArray() {
		// check that no in place mods occur
		Object[] test, defensiveCopy = SPL_original.clone();
		// splice no args: copy
		test = BetterArrays.splice(SPL_original);
		assertArrayEquals(defensiveCopy, SPL_original);
		assertArrayEquals(SPL_normalcp, test);
		// splice 0, -1, -1: reverse copy
		test = BetterArrays.splice(SPL_original, 0, -1, -1);
		assertArrayEquals(defensiveCopy, SPL_original);
		assertArrayEquals(SPL_reversecp, test);
		// splice 0, -1, 2: every other object
		test = BetterArrays.splice(SPL_original, 0, -1, 2);
		assertArrayEquals(defensiveCopy, SPL_original);
		assertArrayEquals(SPL_bigstepcp, test);
		// splice 0, -1, -2: reverse every other object
		test = BetterArrays.splice(SPL_original, 0, -1, -2);
		assertArrayEquals(defensiveCopy, SPL_original);
		assertArrayEquals(SPL_rbigstepcp, test);
	}
}
