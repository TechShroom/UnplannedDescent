/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshoom.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
