/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.yoga;

import static org.lwjgl.util.yoga.Yoga.YGConfigFree;
import static org.lwjgl.util.yoga.Yoga.YGConfigNew;
import static org.lwjgl.util.yoga.Yoga.YGConfigSetLogger;
import static org.lwjgl.util.yoga.Yoga.YGConfigSetPointScaleFactor;
import static org.lwjgl.util.yoga.Yoga.YGConfigSetUseLegacyStretchBehaviour;
import static org.lwjgl.util.yoga.Yoga.YGConfigSetUseWebDefaults;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.libc.LibCStdio;
import org.lwjgl.util.yoga.YGLoggerI;

public class YogaConfig {

    long mNativePointer;
    private YogaLogger mLogger;

    public YogaConfig() {
        mNativePointer = YGConfigNew();
        if (mNativePointer == 0) {
            throw new IllegalStateException("Failed to allocate native memory");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            YGConfigFree(mNativePointer);
        } finally {
            super.finalize();
        }
    }

    private native void YGConfigSetExperimentalFeatureEnabled(
            long nativePointer,
            int feature,
            boolean enabled);

    public void setExperimentalFeatureEnabled(YogaExperimentalFeature feature, boolean enabled) {
        YGConfigSetExperimentalFeatureEnabled(mNativePointer, feature.intValue(), enabled);
    }

    public void setUseWebDefaults(boolean useWebDefaults) {
        YGConfigSetUseWebDefaults(mNativePointer, useWebDefaults);
    }

    public void setPointScaleFactor(float pixelsInPoint) {
        YGConfigSetPointScaleFactor(mNativePointer, pixelsInPoint);
    }

    /**
     * Yoga previously had an error where containers would take the maximum
     * space possible instead of the minimum like they are supposed to. In
     * practice this resulted in implicit behaviour similar to align-self:
     * stretch; Because this was such a long-standing bug we must allow legacy
     * users to switch back to this behaviour.
     */
    public void setUseLegacyStretchBehaviour(boolean useLegacyStretchBehaviour) {
        YGConfigSetUseLegacyStretchBehaviour(mNativePointer, useLegacyStretchBehaviour);
    }

    public void setLogger(YogaLogger logger) {
        mLogger = logger;

        YGLoggerI impl = null;
        if (logger != null) {
            impl = (long config, long node, int level, long format, long args) -> {
                int remaining;
                String message;
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    ByteBuffer buf = stack.calloc(256);
                    remaining = LibCStdio.nvsnprintf(MemoryUtil.memAddress(buf), buf.remaining(), format, args);
                    message = MemoryUtil.memUTF8(buf, remaining);
                }
                logger.log(new YogaNode(node), YogaLogLevel.fromInt(level), message);

                return remaining;
            };
        }
        YGConfigSetLogger(mNativePointer, impl);
    }

    public YogaLogger getLogger() {
        return mLogger;
    }
}
