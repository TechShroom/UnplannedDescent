package com.techshroom.unplanned.core.mod;

import com.google.auto.value.AutoValue;

/**
 * Data about a mod.
 */
@AutoValue
public abstract class ModMetadata {
    
    public static final Builder builder() {
        return new AutoValue_ModMetadata.Builder();
    }

    @AutoValue.Builder
    public interface Builder {
        
        default Builder idAndName(String id) {
            return id(id).name(id);
        }

        Builder id(String id);

        Builder name(String name);

        Builder version(String version);

        Builder buildNumber(long buildNumber);

        Builder targetGameVersion(String targetGameVersion);
        
        ModMetadata build();

    }

    ModMetadata() {
    }

    /**
     * Returns the id of the mod. This id <b>must</b> be unique, if there are
     * non-unique ids there will be a crash!
     * 
     * @return the id of the mod
     */
    public abstract String getId();

    /**
     * Returns the display name of the mod. This may be non-unique.
     * 
     * @return the id of the mod
     */
    public abstract String getName();

    /**
     * Returns the version of the mod. This version may be used on multiple
     * builds of a mod.
     * 
     * @return the version of the mod
     */
    public abstract String getVersion();

    /**
     * Returns the build number of the mod. This must be incremented every time
     * there is a mod upgrade, any changes to the code must increase this
     * number.
     * 
     * @return the build number of the mod
     */
    public abstract long getBuildNumber();

    /**
     * Returns the targeted game version of the mod. A game may choose to load
     * the mod in non-matching versions if the API is non-breaking.
     * 
     * @return the targeted game version of the mod
     */
    public abstract String getTargetGameVersion();

    @Override
    public String toString() {
        return String.format(
                "%s[name=%s,version=%s,buildNumber=%s,targetGameVersion=%s]",
                getId(), getName(), getVersion(), getBuildNumber(),
                getTargetGameVersion());
    }

}
