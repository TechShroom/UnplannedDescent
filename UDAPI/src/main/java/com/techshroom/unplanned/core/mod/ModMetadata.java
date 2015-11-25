package com.techshroom.unplanned.core.mod;

import static com.google.common.base.Preconditions.checkState;

import com.google.auto.value.AutoValue;
import com.techshroom.unplanned.core.util.UDStrings;

/**
 * Data about a mod.
 */
@AutoValue
public abstract class ModMetadata {

    public static final Builder builder() {
        return new AutoValue_ModMetadata.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        Builder() {
        }

        public Builder idAndName(String id) {
            return id(id).name(id);
        }

        public abstract Builder id(String id);

        public abstract Builder name(String name);

        public abstract Builder version(String version);

        public abstract Builder buildNumber(long buildNumber);

        public abstract Builder targetGameVersion(String targetGameVersion);

        abstract ModMetadata autoBuild();

        public ModMetadata build() {
            ModMetadata built = autoBuild();
            // TODO semver checks
            checkState(UDStrings.count(built.getVersion(), '.') >= 2,
                    "version must be semver");
            checkState(!built.getId().isEmpty(), "ID cannot be empty");
            checkState(!built.getName().isEmpty(), "Name cannot be empty");
            checkState(!built.getTargetGameVersion().isEmpty(),
                    "Target game version cannot be empty");
            checkState(built.getBuildNumber() >= 0,
                    "Build number must be positive");
            return built;
        }

    }

    ModMetadata() {
    }

    /**
     * Returns the id of the mod, which cannot be empty. This id <b>must</b> be
     * unique, if there are non-unique ids there will be a crash!
     * 
     * @return the id of the mod
     */
    public abstract String getId();

    /**
     * Returns the display name of the mod, which cannot be empty. This may be
     * non-unique.
     * 
     * @return the id of the mod
     */
    public abstract String getName();

    /**
     * Returns the version of the mod, which MUST be in semver style. This
     * version may be used on multiple builds of a mod.
     * 
     * @return the version of the mod
     */
    public abstract String getVersion();

    /**
     * Returns the build number of the mod, which must be positive. This must be
     * incremented every time there is a mod upgrade, any changes to the code
     * must increase this number.
     * 
     * @return the build number of the mod
     */
    public abstract long getBuildNumber();

    /**
     * Returns the targeted game version of the mod, which may not be empty. A
     * game may choose to load the mod in non-matching versions if the API is
     * non-breaking.
     * 
     * @return the targeted game version of the mod
     */
    public abstract String getTargetGameVersion();

    @Override
    public final String toString() {
        return String.format(
                "%s[name=%s,version=%s,buildNumber=%s,targetGameVersion=%s]",
                getId(), getName(), getVersion(), getBuildNumber(),
                getTargetGameVersion());
    }

}
