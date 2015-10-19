package com.techshroom.unplanned.core.util;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public final class Logging {

    /**
     * <p>
     * These logging groups are used to filter certain information. The default
     * logging combo is INFO + WARNING + ERROR.
     * </p>
     * 
     * Logging groups from lowest to highest: INFO, WARNING, DEBUG, JUNK.<br>
     * <br>
     * 
     * Recommended usages:
     * <dl>
     * <dt>INFO</dt>
     * <dd>- STDOUT</dd>
     * <dt>WARNING</dt>
     * <dd>- warnings like non-fatal OpenGL errors</dd>
     * <dt>ERROR</dt>
     * <dd>- STDERR</dd>
     * <dt>DEBUG</dt>
     * <dd>- debug info for developing</dd>
     * <dt>JUNK</dt>
     * <dd>- for batch-dumping information</dd>
     * </dl>
     */
    public static enum LoggingGroup {
        /**
         * Standard output for users; etc.
         */
        INFO, /**
               * Non-fatal errors or suggestions for performance
               */
        WARNING, /**
                  * Fatal errors
                  */
        ERROR, /**
                * Debug output for developing
                */
        DEBUG, /**
                * Dump group for unloading tons of data
                */
        JUNK;

        public static final Set<LoggingGroup> ALL =
                Sets.immutableEnumSet(EnumSet.allOf(LoggingGroup.class));
    }

    private static Set<LoggingGroup> logGroups = Sets.immutableEnumSet(
            LoggingGroup.INFO, LoggingGroup.WARNING, LoggingGroup.ERROR);

    public static Set<LoggingGroup> getValidGroups() {
        return Sets.immutableEnumSet(logGroups);
    }

    public static Set<LoggingGroup> setValidGroups(Set<LoggingGroup> groups) {
        logGroups = Sets.immutableEnumSet(groups);
        return getValidGroups();
    }

    public static Set<LoggingGroup> setValidGroups(LoggingGroup... groups) {
        return setValidGroups(ImmutableSet.copyOf(groups));
    }

    public static Set<LoggingGroup> setValidGroupRange(LoggingGroup low,
            LoggingGroup high) {
        return setValidGroups(
                Sets.immutableEnumSet(
                        (Iterable<LoggingGroup>) Stream
                                .of(LoggingGroup.values())
                                .filter(x -> low.ordinal() <= x
                                        .ordinal()
                                && x.ordinal() <= high.ordinal())::iterator));
    }

    public static boolean isValidGroup(LoggingGroup g) {
        return getValidGroups().contains(g);
    }
    
    public static void log(String msg, LoggingGroup group) {
        if (!logGroups.contains(group)) {
            return;
        }
        System.err.println("[" + group + "] " + msg);
    }

}
