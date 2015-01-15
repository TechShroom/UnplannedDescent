package com.techshroom.unplanned.core.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public final class Arrays2 {

	public static final int SPLICE_DEFAULT = Integer.MIN_VALUE;

	/**
	 * Splices an array, like in Python.
	 * 
	 * <p>
	 * {@code array[start:end:step]} directly translates to
	 * {@code splice(array, start, end, step)}. Default values are marked with
	 * {@link #SPLICE_DEFAULT}.
	 * </p>
	 * 
	 * <p>
	 * Example translations:
	 * <table border=1 cellspacing=0 cellpadding=3>
	 * <tr>
	 * <td>Python syntax</td>
	 * <td>{@code splice} syntax</td>
	 * </tr>
	 * <tr>
	 * <td>{@code array[:]}</td>
	 * <td>{@code splice(array)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code array[1:]}</td>
	 * <td>{@code splice(array, 1)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code array[:1]}</td>
	 * <td>{@code splice(array, SPLICE_DEFAULT, 1)}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code array[::-1]}</td>
	 * <td>{@code splice(array, SPLICE_DEFAULT, SPLICE_DEFAULT, -1)}</td>
	 * </tr>
	 * </table>
	 * </p>
	 * 
	 * @param <T>
	 *            - array type
	 * @param array
	 *            - array to splice
	 * @param splice
	 *            - splice parameters
	 * @return the new spliced array
	 */
	public static <T> T splice(T array, int... splice) {
		int slen = splice.length;
		int start = 0, end = Array.getLength(array), step = 1;

		if (slen > 0) {
			start = splice[0];
			if (start == SPLICE_DEFAULT) {
				start = 0;
			} else if (start < 0) {
				start = Array.getLength(array) - start;
			}
		}
		if (slen > 1) {
			end = splice[1];
			if (end == SPLICE_DEFAULT) {
				end = Array.getLength(array);
			} else if (end < 0) {
				end = Array.getLength(array) - end;
			}
		}
		if (slen > 2) {
			step = splice[2];
			if (step == SPLICE_DEFAULT) {
				step = 1;
			}
		}

		if (start < 0) {
			throw new IndexOutOfBoundsException("start < 0");
		}
		if (end > Array.getLength(array)) {
			throw new IndexOutOfBoundsException("end > length");
		}
		if (end < start) {
			throw new IllegalArgumentException("start < end");
		}
		if (step == 0) {
			throw new IllegalArgumentException("step == 0");
		}
		int len = end - start; // get length when step == 1
		boolean back = step < 0;
		step = Math.abs(step);
		if (step > 1) {
			int mod = len % step; // modulo to get leftovers
			len -= mod; // remove them to floor the result
			len /= step; // divide by step
		}
		@SuppressWarnings("unchecked")
		T out = (T) Array.newInstance(array.getClass().getComponentType(), len);
		if (back) {
			for (int i = end - 1, index = 0; i >= start; i -= step, index++) {
				Array.set(out, index, Array.get(array, i));
			}
		} else {
			if (step == 1) {
				System.arraycopy(array, start, out, 0, len);
			} else {
				for (int i = start, index = 0; i < end; i += step, index++) {
					Array.set(out, index, Array.get(array, i));
				}
			}
		}
		return out;
	}

	/**
	 * Creates a new primitive array, avoiding the scary unchecked cast.
	 * 
	 * @param component
	 *            - primitive component type
	 * @param len
	 *            - the length of the array
	 * @return the new array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newPrimitiveArray(Class<?> component, int len) {
		checkArgument(component.isPrimitive(), "use newArray for non-primitive classes");
		return (T) Array.newInstance(component, len);
	}

	/**
	 * Creates a new array, avoiding the scary unchecked cast.
	 * 
	 * @param component
	 *            - component type
	 * @param len
	 *            - the length of the array
	 * @return the new array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(Class<T> component, int len) {
		checkArgument(!component.isPrimitive(), "use newPrimitiveArray for primitive classes");
		return (T[]) Array.newInstance(component, len);
	}

	/**
	 * Gets a boolean argument safely
	 * 
	 * @param args
	 *            - the args list from which to retrieve the argument
	 * @param index
	 *            - the index of the wanted argument
	 * @param def
	 *            - a default value to fallback on
	 * @return the wanted boolean argument value, or the default value
	 */
	public static boolean getArgB(String[] args, int index, boolean def) {
		return Boolean.parseBoolean(getArgS(args, index, Boolean.valueOf(def).toString()));
	}

	/**
	 * Gets a integer argument safely
	 * 
	 * @param args
	 *            - the args list from which to retrieve the argument
	 * @param index
	 *            - the index of the wanted argument
	 * @param def
	 *            - a default value to fallback on
	 * @return the wanted integer argument value, or the default value
	 */
	public static int getArgI(String[] args, int index, int def) {
		return Integer.parseInt(getArgS(args, index, Integer.valueOf(def).toString()));
	}

	/**
	 * Gets a float argument safely
	 * 
	 * @param args
	 *            - the args list from which to retrieve the argument
	 * @param index
	 *            - the index of the wanted argument
	 * @param def
	 *            - a default value to fallback on
	 * @return the wanted float argument value, or the default value
	 */
	public static float getArgF(String[] args, int index, float def) {
		return Float.parseFloat(getArgS(args, index, Float.valueOf(def).toString()));
	}

	/**
	 * Gets a double argument safely
	 * 
	 * @param args
	 *            - the args list from which to retrieve the argument
	 * @param index
	 *            - the index of the wanted argument
	 * @param def
	 *            - a default value to fallback on
	 * @return the wanted double argument value, or the default value
	 */
	public static double getArgD(String[] args, int index, double def) {
		return Double.parseDouble(getArgS(args, index, Double.valueOf(def).toString()));
	}

	/**
	 * Gets a String argument safely
	 * 
	 * @param args
	 *            - the args list from which to retrieve the argument
	 * @param index
	 *            - the index of the wanted argument
	 * @param def
	 *            - a default value to fallback on
	 * @return the wanted String argument value, or the default value
	 */
	public static String getArgS(String[] args, int index, String def) {
		return getArg(args, index, def);
	}

	/**
	 * Gets an argument safely
	 * 
	 * @param args
	 *            - the args list from which to retrieve the argument
	 * @param index
	 *            - the index of the wanted argument
	 * @param def
	 *            - a default value to fallback on
	 * @return the wanted argument value, or the default value
	 */
	public static <T> T getArg(T[] args, int index, T def) {
		if (args == null) {
			return def;
		}
		return args.length <= index ? def : args[index] == null ? def : args[index];
	}
}