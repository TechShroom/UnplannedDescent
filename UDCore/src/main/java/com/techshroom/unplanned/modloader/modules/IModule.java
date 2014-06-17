package com.techshroom.unplanned.modloader.modules;

import com.techshroom.unplanned.modloader.IMod;
import com.techshroom.unplanned.modloader.SkeletalMod;

/**
 * A to be implemented module system for EL. Used like the mods system, but is
 * less of a public API than that. Things in the module system should not be
 * relied upon.
 * 
 * @author Kenzie Togami
 */
public abstract class IModule extends SkeletalMod implements IMod {
}
