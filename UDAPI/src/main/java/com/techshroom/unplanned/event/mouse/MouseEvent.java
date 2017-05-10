package com.techshroom.unplanned.event.mouse;

import com.techshroom.unplanned.event.Event;
import com.techshroom.unplanned.input.Mouse;

public interface MouseEvent extends Event {

    Mouse getSource();

}
