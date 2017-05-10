package com.techshroom.unplanned.examples;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;

import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector2i;
import com.google.common.collect.Streams;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.matrix.Matrices;
import com.techshroom.unplanned.event.Event;
import com.techshroom.unplanned.event.window.WindowResizeEvent;
import com.techshroom.unplanned.window.Window;
import com.techshroom.unplanned.window.WindowSettings;

public class ExamplePicker {

    private static final Map<String, Example> EXAMPLES;
    static {
        EXAMPLES = Streams.stream(ServiceLoader.load(Example.class))
                .collect(toImmutableMap(Example::getName, Function.identity()));
    }

    private Matrix4f proj;
    private Vector2i windowSize;

    public static void main(String[] args) {
        new ExamplePicker().run();
    }

    public void run() {
        Event.BUS.register(this);

        Window window = WindowSettings.builder()
                .screenSize(1024, 768)
                .title("Example Picker")
                .build().createWindow();

        GraphicsContext ctx = window.getGraphicsContext();

        ctx.makeActiveContext();
        window.setVsyncOn(true);
        window.setVisible(true);

        Vector2i size = window.getSize();
        resize(WindowResizeEvent.create(window, size.getX(), size.getY()));

        while (!window.isCloseRequested()) {
            window.processEvents();
            ctx.clearGraphicsState();

            ctx.swapBuffers();
        }

        window.destroy();
    }

    @Subscribe
    public void resize(WindowResizeEvent event) {
        windowSize = event.getSize();
        int w = windowSize.getX();
        int h = windowSize.getY();
        proj = Matrices.orthographicProjection(w, h, -1000, 1000);
    }

}
