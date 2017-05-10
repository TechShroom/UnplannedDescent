package com.techshroom.unplanned.event.keyboard;

import com.techshroom.unplanned.event.Event;
import com.techshroom.unplanned.input.Keyboard;

public interface KeyboardEvent extends Event {

    Keyboard getSource();

}
