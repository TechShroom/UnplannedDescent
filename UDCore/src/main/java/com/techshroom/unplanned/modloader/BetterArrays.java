package com.techshroom.unplanned.modloader;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public final class BetterArrays {
    public static void print(Object[] array) {
        print(array, System.err);
    }

    public static void print(Object[] array, PrintStream o) {
        if (array == null) {
            o.println("null");
        }
        o.println(array.getClass().getComponentType().getName()
                + "[] (length: " + array.length + ") contents:");
        int index = 0;
        for (Object object : array) {
            String out = "";
            String indexs = index + ": ";
            String clazs = "";
            if (object == null) {
                out = "<null object>";
                clazs = array.getClass().getComponentType().getName();
            } else if (object instanceof String && ((String) object).equals("")) {
                out = "<empty string>";
                clazs = object.getClass().getName();
            } else {
                out = object.toString();
                clazs = object.getClass().getName();
            }
            o.println(indexs + "(" + clazs + ") " + out);
            index++;
        }
    }

    public static <T, D> T[] arrayTranslate(Class<T> generic, D[] src) {
        ArrayList<T> temp = new ArrayList<T>(src.length);
        for (D o : src) {
            if (o != null && generic.isAssignableFrom(o.getClass())) {
                temp.add(generic.cast(o));
            } else if (o == null) {
                temp.add((T) null);
            } else {
                System.err.println("Lost " + o + " because it was not of type "
                        + generic.getName());
            }
        }
        T[] arrType = newArray(generic, src.length);
        return temp.toArray(arrType);
    }

    public static <T> T[] randomArray(T[] in) {
        if (in.length == 0 || in.length == 1) {
            return in;
        }
        T[] test = newArray(in.getClass().getComponentType(), in.length);
        boolean solved = false;
        boolean[] taken = new boolean[test.length];
        int total = test.length;
        Random r = new Random(new Random().nextInt());
        int index = 0;
        while (!solved) {
            int ra = r.nextInt(test.length);
            if (!taken[ra]) {
                test[ra] = in[index];
                taken[ra] = true;
                index++;
                total--;
            }
            if (total == 0) {
                solved = true;
            }
        }
        return test;
    }

    public static void dump(Object[] array) {
        for (Object t : array) {
            System.out.println(t);
        }
    }

    public static <T> T[] repeatRandomArray(T[] in, int count) {
        T[] array = newArray(in.getClass().getComponentType(), in.length);
        System.arraycopy(in, 0, array, 0, in.length);
        while (count > -1) {
            array = BetterArrays.randomArray(array);
            count--;
        }
        return array;
    }

    public static String dump0(Object[] array) {
        if (array == null) {
            return "<null array>";
        }
        if (array.length == 0) {
            return "[]";
        }
        String ret = "[";
        for (Object o : array) {
            if (o instanceof String) {
                o = "'" + o + "'";
            }
            ret += o + ", ";
        }
        ret = ret.substring(0, ret.length() - 2) + "]";
        return ret;
    }

    public static byte[] intToByteArray(int value) {
        return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
                (byte) (value >>> 8), (byte) value };
    }

    public static <T> T[] reverse(T[] stuff) {
        T[] out = newArray(stuff.getClass().getComponentType(), stuff.length);
        for (int i = 0; i < stuff.length; i++) {
            out[stuff.length - i - 1] = stuff[i];
        }
        return out;
    }

    /*
     * This is a cool trick which allows us to return the requested array even
     * with primitives as the component type.
     */
    public static <T> T reverseNonGeneric(T t) {
        int tlen = Array.getLength(t);
        T out = newArray(t.getClass().getComponentType(), tlen);
        for (int i = 0; i < tlen; i++) {
            Array.set(out, tlen - i - 1, Array.get(t, i));
        }
        return out;
    }

    public static void fillArray(Object array, Object value) {
        for (int i = 0; i < Array.getLength(array); i++) {
            Array.set(array, i, value);
        }
    }

    public static <T> T createAndFill(Class<?> type, int size, Object value) {
        T array = newArray(type, size);
        fillArray(array, value);
        return array;
    }

    public static <T> T splice(T array, int... splice) {
        int slen = splice.length;
        int start = 0, end = Array.getLength(array), step = 1;

        if (slen > 0) {
            start = splice[0];
        }
        if (slen > 1) {
            end = splice[1];
            if (end < 0) {
            	end = Array.getLength(array);
            }
        }
        if (slen > 2) {
            step = splice[2];
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
        if (step > 1) {
            int mod = len % step; // modulo to get leftovers
            len -= mod; // remove them to floor the result
            len /= step; // divide by step
        }
        T out = newArray(array.getClass().getComponentType(), len);
        if (step < 0) {
            step = -step;
            for (int i = end - 1, index = 0; i >= start; i -= step, index++) {
                Array.set(out, index, Array.get(array, i));
            }
        } else {
            for (int i = start, index = 0; i < end; i += step, index++) {
                Array.set(out, index, Array.get(array, i));
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public static <T> T newArray(Class<?> component, int len) {
        return (T) Array.newInstance(component, len);
    }
}