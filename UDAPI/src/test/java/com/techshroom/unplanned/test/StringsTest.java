package com.techshroom.unplanned.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.techshroom.unplanned.core.util.UDStrings;

public class StringsTest {

    @Test
    public void testCountEmptyString() throws Exception {
        assertEquals(0, UDStrings.count("", ' '));
    }

    @Test
    public void testCountNotPresent() throws Exception {
        assertEquals(0, UDStrings.count("foo", ' '));
    }

    @Test
    public void testCount() throws Exception {
        assertEquals(1, UDStrings.count(" ", ' '));
    }

    @Test
    public void testNumberToChar() throws Exception {
        for (char c = '0', i = 0; c <= '9' && i <= 9; c++, i++) {
            assertEquals(i, UDStrings.getNumForChar(c));
        }
    }

    @Test
    public void testCharToNumber() throws Exception {
        for (char c = '0', i = 0; c <= '9' && i <= 9; c++, i++) {
            assertEquals(c, UDStrings.getCharForNum((byte) i));
        }
    }

    @Test
    public void testPrettyJoin() throws Exception {
        String join = "~";
        String lastPrefix = "#";
        assertEquals("1", UDStrings.prettyJoin(join, lastPrefix, "1"));
        assertEquals("1 # 2", UDStrings.prettyJoin(join, lastPrefix, "1", "2"));
        assertEquals("1~ 2~ # 3",
                UDStrings.prettyJoin(join, lastPrefix, "1", "2", "3"));
        assertEquals("1~ 2~ 3~ 4~ # 5",
                UDStrings.prettyJoin(join, lastPrefix, "1", "2", "3", "4", "5"));
    }

    @Test
    public void testPrettyJoinAnd() throws Exception {
        assertEquals("1", UDStrings.prettyJoinAnd("1"));
        assertEquals("1 and 2", UDStrings.prettyJoinAnd("1", "2"));
        assertEquals("1, 2, and 3", UDStrings.prettyJoinAnd("1", "2", "3"));
        assertEquals("1, 2, 3, 4, and 5",
                UDStrings.prettyJoinAnd("1", "2", "3", "4", "5"));
    }

    @Test
    public void testPrettyJoinOr() throws Exception {
        assertEquals("1", UDStrings.prettyJoinOr("1"));
        assertEquals("1 or 2", UDStrings.prettyJoinOr("1", "2"));
        assertEquals("1, 2, or 3", UDStrings.prettyJoinOr("1", "2", "3"));
        assertEquals("1, 2, 3, 4, or 5",
                UDStrings.prettyJoinOr("1", "2", "3", "4", "5"));
    }

}
