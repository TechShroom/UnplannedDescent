package com.techshroom.unplanned.core.util;

public class Strings {

    /**
     * Returns the amount of <tt>c</tt>'s in <tt>s</tt>
     * 
     * @param s
     *            - the string to check
     * @param c
     *            - the char to count
     * @return the number of <tt>c</tt>'s in <tt>s</tt>
     */
    public static int count(String s, char c) {
        char[] ca = s.toCharArray();
        int cnt = 0;
        for (char x : ca) {
            if (x == c) {
                cnt++;
            }
        }
        return cnt;
    }

    /**
     * Returns the matching <tt>char</tt> for <tt>number</tt>. Only works with
     * numbers 0-9.
     * 
     * @param number
     *            - a number from 0-9
     * @return the char that matches
     */
    public static char getCharForNum(byte number) {
        if (number > 9) {
            throw new IndexOutOfBoundsException("number > 9");
        }
        return (char) (number + '0');
    }

    /**
     * Returns the matching <tt>number</tt> for <tt>c</tt>. Only works with
     * chars '0'-'9'.
     * 
     * @param c
     *            - a char from '0'-'9'
     * @return the char that matches
     */
    public static byte getNumForChar(char c) {
        if (c < '0' || c > '9') {
            throw new IndexOutOfBoundsException("not 0-9");
        }
        return (byte) (c - '0');
    }
}
