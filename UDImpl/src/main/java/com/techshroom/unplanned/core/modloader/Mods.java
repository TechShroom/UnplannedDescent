package com.techshroom.unplanned.core.modloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.techshroom.unplanned.core.modloader.modules.ModuleSystem;
import com.techshroom.unplanned.core.util.LUtils;

public final class Mods {
	private static ArrayList<IMod> loaded = new ArrayList<IMod>();
	private static boolean loaded_mods = false;

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
		ArrayList<IMod> injected = ModInjector.findAndInject();
		System.err.println("Loaded mods from classpath.");
		loaded.addAll(injected);
		// System.err.println("Initializing mods...");
		// System.err.println("Complete.");
		loaded = injected;
		loaded.trimToSize();
		loaded_mods = true;
		System.err.println("UD Mod System loaded.");
		ModuleSystem.loadModulesFromMods();
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
			try {
				ClassPathHack.addFile(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static List<IMod> getLoadedMods() {
		if (!hasLoadedMods()) {
			throw new IllegalStateException(
					"Getting loaded mods list before loading mods!");
		}
		return Collections.unmodifiableList(loaded);
	}

	public static boolean hasLoadedMods() {
		return loaded_mods;
	}
}
