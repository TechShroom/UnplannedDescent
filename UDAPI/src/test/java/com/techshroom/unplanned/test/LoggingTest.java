package com.techshroom.unplanned.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Test;

import com.techshroom.unplanned.core.util.LUtils;
import com.techshroom.unplanned.core.util.Logging;
import com.techshroom.unplanned.core.util.Logging.LoggingGroup;

public class LoggingTest {

    /**
     * Logging can limit the printing to certain groups.
     */
    @Test
    public void limitsGroups() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // fool LUtils into replacing the wrong streams so our life is easy.
        LUtils.init();
        PrintStream original = System.err;
        System.setErr(new PrintStream(out));
        for (Iterator<LoggingGroup> itr = LoggingGroup.ALL.iterator(); itr
                .hasNext();) {
            LoggingGroup g = itr.next();
            Logging.setValidGroups(g);
            Logging.log("YES", g);
            Set<LoggingGroup> groups = LoggingGroup.ALL.stream()
                    .filter(Predicate.isEqual(g).negate())
                    .collect(Collectors.toSet());
            for (LoggingGroup nonGroup : groups) {
                Logging.log("NO", nonGroup);
            }
        }
        // ensure all written
        System.err.flush();
        System.setErr(original);
        String str = out.toString();
        assertEquals("Logged wrong group: ", str, str.replace("NO", ""));
    }

}
