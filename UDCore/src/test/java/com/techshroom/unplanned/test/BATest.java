package com.techshroom.unplanned.test;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

import com.techshroom.unplanned.modloader.BetterArrays;

public class BATest {

	private Object[] array, reverse;
	private int[] arrayi, reversei;

	@Before
	public void setUp() throws Exception {
		Object obj = new Object();
		array = new Object[] { "abc", 1, 2, 3, obj };
		reverse = new Object[] { obj, 3, 2, 1, "abc" };
		arrayi = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		reversei = new int[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
	}

	/**
	 * BetterArrays can reverse an array.
	 */
	@Test
	public void reversesArray() {
		Object[] reversed_ba = BetterArrays.reverse(array);
		assertArrayEquals(reverse, reversed_ba);
	}

	/**
	 * BetterArrays can reverse a non-generic array.
	 */
	@Test
	public void reversesNonGenericArray() {
		int[] reversed_ba = BetterArrays.reverseNonGeneric(arrayi);
		assertArrayEquals(reversei, reversed_ba);
	}
}
