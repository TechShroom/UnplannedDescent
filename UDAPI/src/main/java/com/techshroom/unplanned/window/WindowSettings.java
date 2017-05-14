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
package com.techshroom.unplanned.window;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;
import java.util.ServiceLoader;

import javax.annotation.Nullable;

import com.flowpowered.math.vector.Vector2i;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class WindowSettings {

    private static final WindowGenerator WINDOW_GENERATOR =
            ServiceLoader.load(WindowGenerator.class).iterator().next();

    public static final Builder builder() {
        Vector2i size = WINDOW_GENERATOR.getDefaultFullscreenSize();
        return new AutoValue_WindowSettings.Builder().screenSize(size).monitor((Monitor) null)
                .sharedWindow((Window) null).fullScreen(false).resizable(true);
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public final Builder screenSize(int width, int height) {
            return screenSize(new Vector2i(width, height));
        }

        public abstract Builder screenSize(Vector2i size);

        public abstract Builder title(String title);

        public abstract Builder fullScreen(boolean fullscreen);

        public abstract Builder resizable(boolean resizable);

        public final Builder monitor(@Nullable Monitor monitor) {
            return monitor(Optional.ofNullable(monitor));
        }

        public final Builder usePrimaryMonitor() {
            return monitor(WindowSystem.getInstance().getPrimaryMonitor());
        }

        public abstract Builder monitor(Optional<Monitor> monitor);

        public final Builder sharedWindow(@Nullable Window sharedWindow) {
            return sharedWindow(Optional.ofNullable(sharedWindow));
        }

        public abstract Builder sharedWindow(Optional<Window> sharedWindow);

        abstract WindowSettings autoBuild();

        public WindowSettings build() {
            WindowSettings settings = autoBuild();
            checkState(
                    !settings.isFullScreen()
                            || settings.getMonitor().isPresent(),
                    "settings must have a monitor for fullscreen");
            return settings;
        }

    }

    WindowSettings() {
    }

    public abstract Vector2i getScreenSize();

    public abstract String getTitle();

    public abstract boolean isFullScreen();

    public abstract boolean isResizable();

    public abstract Optional<Monitor> getMonitor();

    public abstract Optional<Window> getSharedWindow();

    public abstract Builder toBuilder();

    public final Window createWindow() {
        return WINDOW_GENERATOR.generateWindow(this);
    }

}
