package com.techshroom.unplanned.core.mod;

import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.techshroom.unplanned.core.util.Strings;

/**
 * Thrown when two or more mods have the same ID.
 */
public class ModIDConflictException extends RuntimeException {

    private static final long serialVersionUID = 8008495944986210861L;

    private static String
            generateConflictMessage(Multimap<String, ModMetadata> conflicts) {
        return conflicts.asMap().values().stream()
                .map(v -> "Mods " + Strings.prettyJoinAnd(v))
                .collect(Collectors.joining(", ", "Conflicts: ", ""));
    }

    private final Multimap<String, ModMetadata> conflictingIds;

    public ModIDConflictException(Multimap<String, ModMetadata> conflicts) {
        super("Mod ID conflict: " + generateConflictMessage(conflicts));
        this.conflictingIds = conflicts;
    }

    public Multimap<String, ModMetadata> getConflictingIds() {
        return this.conflictingIds;
    }

}
