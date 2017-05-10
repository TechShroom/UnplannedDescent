package com.techshroom.unplanned.event.window;

import com.flowpowered.math.vector.Vector2i;
import com.google.auto.value.AutoValue;
import com.techshroom.unplanned.window.Window;

@AutoValue
public abstract class WindowFramebufferResizeEvent implements WindowEvent {

    public static WindowFramebufferResizeEvent create(Window source, int width, int height) {
        return new AutoValue_WindowFramebufferResizeEvent(source, new Vector2i(width, height));
    }

    WindowFramebufferResizeEvent() {
    }

    public abstract Vector2i getSize();

}
