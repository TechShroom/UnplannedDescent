package com.techshroom.unplanned.window;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;
import java.util.ServiceLoader;

import javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.techshroom.unplanned.monitor.Monitor;
import com.techshroom.unplanned.value.Dimension;

@AutoValue
public abstract class WindowSettings {

    private static final WindowGenerator WINDOW_GENERATOR = ServiceLoader
            .load(WindowGenerator.class).iterator().next();

    public static final Builder builder() {
        return new AutoValue_WindowSettings.Builder().monitorRaw(null)
                .sharedWindowRaw(null).fullScreen(false);
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public Builder screenSize(int width, int height) {
            return screenSize(Dimension.of(width, height));
        }

        public abstract Builder screenSize(Dimension size);

        public abstract Builder title(String title);

        public abstract Builder fullScreen(boolean fullscreen);

        public Builder monitorRaw(@Nullable Monitor monitor) {
            return monitor(Optional.ofNullable(monitor));
        }

        public abstract Builder monitor(Optional<Monitor> monitor);

        public Builder sharedWindowRaw(@Nullable Window sharedWindow) {
            return sharedWindow(Optional.ofNullable(sharedWindow));
        }

        public abstract Builder sharedWindow(Optional<Window> sharedWindow);

        abstract WindowSettings autoBuild();

        public WindowSettings build() {
            WindowSettings settings = autoBuild();
            checkState(!settings.isFullScreen()
                    || settings.getMonitor().isPresent(),
                    "settings must have a monitor for fullscreen");
            return settings;
        }

    }

    WindowSettings() {
    }

    public abstract Dimension getScreenSize();

    public abstract String getTitle();

    public abstract boolean isFullScreen();

    public abstract Optional<Monitor> getMonitor();

    public abstract Optional<Window> getSharedWindow();

    public final Builder toBuilder() {
        return new AutoValue_WindowSettings.Builder(this);
    }

    public final Window createWindow() {
        return WINDOW_GENERATOR.generateWindow(getScreenSize(), getTitle(),
                isFullScreen() ? getMonitor().get() : null, getSharedWindow()
                        .orElse(null));
    }

}
