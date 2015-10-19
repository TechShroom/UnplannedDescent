package com.techshroom.unplanned.monitor;

import java.util.ServiceLoader;

class MonitorProviderReferenceHolder {

    static final MonitorProvider REFERENCE = ServiceLoader
            .load(MonitorProvider.class).iterator().next();

}
