package com.techshroom.unplanned.monitor;

import java.util.List;

public interface MonitorProvider {

    static MonitorProvider getInstance() {
        return MonitorProviderReferenceHolder.REFERENCE;
    }

    Monitor getPrimaryMonitor();

    List<Monitor> getMonitors();

}
