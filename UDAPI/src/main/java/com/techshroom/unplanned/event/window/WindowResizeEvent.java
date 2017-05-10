package com.techshroom.unplanned.event.window;

import com.flowpowered.math.vector.Vector2i;
import com.google.auto.value.AutoValue;
import com.techshroom.unplanned.window.Window;

@AutoValue
public abstract class WindowResizeEvent implements WindowEvent {

    public static WindowResizeEvent create(Window source, int width, int height) {
        return new AutoValue_WindowResizeEvent(source, new Vector2i(width, height));
    }

    WindowResizeEvent() {
    }

    public abstract Vector2i getSize();

}
