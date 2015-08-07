package com.techshroom.unplanned.core.mod;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.core.util.ClassPathHack;
import com.techshroom.unplanned.core.util.LUtils;

/**
 * Access point for mods. This is the class that handles all loading for mods.
 * 
 * @author Kenzie Togami
 */
public final class Mods {

    private static List<IMod> loaded;
    private static boolean loadedMods = false;

    /**
     * Load any mods in the class path, injecting the mods in the mod folder
     * into the classpath.
     */
    public static void findAndLoad() {
        if (hasLoadedMods()) {
            throw new IllegalStateException(
                    "Mods already loaded, cannot reload");
        }
        System.err.println("UD Mod System starting...");
        if (!injectModsFolder()) {
            System.err.println(
                    "[WARNING] Mods folder does not exist or is a file, "
                            + "add it if you want mods to be loaded from there.");
        }
        List<IMod> injected = ModLoader.loadModsFromClasspath();
        System.err.println("Loaded mods from classpath.");
        loaded = ImmutableList.copyOf(injected);
        System.err.println("Initializing mods...");
        System.err.println("Complete.");
        loadedMods = true;
        System.err.println("UD Mod System loaded.");
    }

    private static boolean injectModsFolder() {
        String topLevel = LUtils.ROOT;
        File mods = new File(topLevel, "mods");
        if (!loadDirectory(mods)) {
            return false;
        }
        System.err.println("Injected '" + mods + "/**' into classpath.");
        return true;
    }

    private static boolean loadDirectory(File dir) {
        if (!dir.exists() || dir.isFile()) {
            return false;
        }

        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                loadDirectory(f);
                continue;
            }

            try {
                ClassPathHack.addFile(f);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    /**
     * Gets the list of loaded mods.
     * 
     * @return the list of loaded mods
     * @throws IllegalStateException
     *             If {@link #findAndLoad()} has not been called yet, and there
     *             are no mods loaded
     */
    public static List<IMod> getLoadedMods() {
        if (!hasLoadedMods()) {
            throw new IllegalStateException(
                    "Getting loaded mods list before loading mods!");
        }
        return ImmutableList.copyOf(loaded);
    }

    /**
     * Returns {@code true} if mods have already been loaded.
     * 
     * @return {@code true} if mods have already been loaded
     */
    public static boolean hasLoadedMods() {
        return loadedMods;
    }
}
