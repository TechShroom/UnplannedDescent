package com.techshroom.unplanned.core.mod;

/**
 * Base class for {@link Mod}.
 * 
 * @author Kenzie Togami
 */
public abstract class SkeletalMod implements Mod {
    
    protected final ModMetadata data;
    
    protected SkeletalMod(ModMetadata data) {
        this.data = data;
    }
    
    @Override
    public ModMetadata getMetadata() {
        return this.data;
    }
    
}
