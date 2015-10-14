package com.techshroom.unplanned.core.mod;

import java.util.List;
import java.util.ServiceLoader;

import com.google.common.collect.ImmutableList;

final class ModLoader {

    /**
     * Scans the classpath and loads any mods it can find into the game. The
     * list is used by {@link Mods} to get the initial listings.
     * 
     * @return a list of the loaded mods
     */
    static List<ModProvider> loadModsFromClasspath() {
        return ImmutableList.copyOf(ServiceLoader.load(ModProvider.class));
    }
}
