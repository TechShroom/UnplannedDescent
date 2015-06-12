package com.techshroom.unplanned.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import com.techshroom.unplanned.core.util.LUtils;
import com.techshroom.unplanned.core.util.LUtils.LoggingGroup;

public class UtilityTest {

    /**
     * LUtils can limit the printing to certain groups.
     */
    @Test
    public void limitsGroups() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // fool it into replacing the wrong streams so our life is easy.
        LUtils.init();
        PrintStream original = System.err;
        System.setErr(new PrintStream(out));
        for (Iterator<LoggingGroup> itr = LoggingGroup.ALL.iterator(); itr
                .hasNext();) {
            LoggingGroup g = itr.next();
            LUtils.setValidGroups(g);
            LUtils.print("YES", g);
            LUtils.print("NO", getRandomNotProvided(LoggingGroup.ALL, g));
        }
        // ensure all written
        System.err.flush();
        System.setErr(original);
        String str = out.toString();
        assertEquals("Logged wrong group: ", str, str.replace("NO", ""));
    }

    private <T> T getRandomNotProvided(Set<T> all, T g) {
        Iterator<T> itr = all.iterator();
        if (!itr.hasNext()) {
            return null;
        }
        T first = itr.next();
        if (g == first) {
            return itr.hasNext() ? itr.next() : null;
        }
        return first;
    }
}
