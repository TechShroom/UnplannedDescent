package com.techshroom.unplanned.core.mod.modules;

import com.techshroom.unplanned.core.mod.Mod;
import com.techshroom.unplanned.core.mod.ModMetadata;
import com.techshroom.unplanned.core.mod.SkeletalMod;

/**
 * A to-be-implemented module system for UD. Used like the mods system, but is
 * less of a public API than that. Things in the module system should not be
 * relied upon.
 * 
 * @author Kenzie Togami
 */
public abstract class Module extends SkeletalMod implements Mod {

    protected Module(ModMetadata data) {
        super(data);
    }

    // nothing here yet
}
