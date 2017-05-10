package com.techshroom.unplanned.event;

import com.google.common.eventbus.EventBus;

/**
 * Root event.
 */
public interface Event {
    
    EventBus BUS = new EventBus("primary");

}
