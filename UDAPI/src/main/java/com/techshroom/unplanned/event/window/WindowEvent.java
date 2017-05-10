package com.techshroom.unplanned.event.window;

import com.techshroom.unplanned.event.Event;
import com.techshroom.unplanned.window.Window;

public interface WindowEvent extends Event {
    
    Window getSource();

}
