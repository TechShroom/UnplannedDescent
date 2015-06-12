package com.techshroom.unplanned.core.modloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
//import com.techshroom.unplanned.core.modloader.modules.ModuleSystem;
import com.techshroom.unplanned.core.util.LUtils;

/**
 * Access point for mods. This is the class that handles all loading for mods.
 * 
 * @author Kenzie Togami
 */
public final class Mods {

    private static ArrayList<IMod> loaded = new ArrayList<IMod>();
    private static boolean loadedMods = false;

    /**
     * Load any mods in the class path, injecting the mods in the mod folder
     * into the classpath.
     */
    public static void findAndLoad() {
        if (hasLoadedMods()) {
            System.err
                    .println("Already loaded mod system, trying to load again?");
            return;
        }
        System.err.println("UD Mod System starting...");
        if (!injectModsFolder()) {
            System.err
                    .println("[WARNING] Mods folder does not exist or is a file, "
                            + "add it if you want mods to be loaded from there.");
        }
        // ArrayList<IMod> injected = ModInjector.findAndInject();
        System.err.println("Loaded mods from classpath.");
        // loaded.addAll(injected);
        // System.err.println("Initializing mods...");
        // System.err.println("Complete.");
        // loaded = injected;
        loaded.trimToSize();
        loadedMods = true;
        System.err.println("UD Mod System loaded.");
        // ModuleSystem.loadModulesFromMods();
    }

    private static boolean injectModsFolder() {
        String topLevel = LUtils.TOP_LEVEL;
        File mods = new File(topLevel, "mods");
        if (!loadDirectory(mods)) {
            return false;
        }
        System.err.println("Injected '" + mods + "' into classpath.");
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
            /*
             * try { //ClassPathHack.addFile(f); } catch (IOException e) {
             * e.printStackTrace(); }
             */
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
