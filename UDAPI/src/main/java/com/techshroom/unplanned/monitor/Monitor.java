package com.techshroom.unplanned.monitor;

import java.util.List;
import java.util.function.BiConsumer;

import com.techshroom.unplanned.pointer.Pointer;
import com.techshroom.unplanned.value.GammaRamp;
import com.techshroom.unplanned.value.Point;
import com.techshroom.unplanned.value.VideoMode;

public interface Monitor {

    enum Event {

        CONNECTED, DISCONNECTED;

    }

    List<VideoMode> getSupportedVideoModes();

    VideoMode getVideoMode();

    String getTitle();

    Point getLocation();

    GammaRamp getGammaRamp();

    void setGammaRamp(GammaRamp ramp);

    void setMonitorCallback(BiConsumer<Monitor, Event> callback);

    Pointer getMonitorPointer();

}
