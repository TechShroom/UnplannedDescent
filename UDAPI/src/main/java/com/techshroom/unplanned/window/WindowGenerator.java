package com.techshroom.unplanned.window;

import javax.annotation.Nullable;

import com.techshroom.unplanned.monitor.Monitor;
import com.techshroom.unplanned.value.Dimension;

/**
 * Generator for window objects, part of the internal API.
 * 
 * @author Kenzie Togami
 */
public interface WindowGenerator {

    Window generateWindow(Dimension size, String title,
            @Nullable Monitor monitor, @Nullable Window share);

}
