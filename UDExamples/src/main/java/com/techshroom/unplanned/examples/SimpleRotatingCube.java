/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshoom.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.techshroom.unplanned.examples;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.flowpowered.math.imaginary.Quaternionf;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.google.auto.service.AutoService;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.Shape;
import com.techshroom.unplanned.blitter.binding.Bindable;
import com.techshroom.unplanned.blitter.matrix.Matrices;
import com.techshroom.unplanned.blitter.shapers.CubeLayout;
import com.techshroom.unplanned.blitter.textures.Downscaling;
import com.techshroom.unplanned.blitter.textures.Texture;
import com.techshroom.unplanned.blitter.textures.TextureData;
import com.techshroom.unplanned.blitter.textures.TextureSettings;
import com.techshroom.unplanned.blitter.textures.TextureWrap;
import com.techshroom.unplanned.blitter.textures.Upscaling;
import com.techshroom.unplanned.blitter.textures.loader.ColorTextureLoader;
import com.techshroom.unplanned.blitter.textures.loader.ColorTextureSpec;
import com.techshroom.unplanned.blitter.textures.loader.StandardTextureLoaders;
import com.techshroom.unplanned.blitter.textures.map.TextureAtlas;
import com.techshroom.unplanned.blitter.textures.map.TextureCollection;
import com.techshroom.unplanned.event.Event;
import com.techshroom.unplanned.event.keyboard.KeyState;
import com.techshroom.unplanned.event.keyboard.KeyStateEvent;
import com.techshroom.unplanned.event.mouse.MouseMoveEvent;
import com.techshroom.unplanned.event.window.WindowResizeEvent;
import com.techshroom.unplanned.input.Key;
import com.techshroom.unplanned.window.Window;
import com.techshroom.unplanned.window.WindowSettings;

@AutoService(Example.class)
public class SimpleRotatingCube extends Example {

    private Window window;
    private Matrix4f proj;
    private Vector2f cursorTotalDx = new Vector2f(0, 0);

    public static void main(String[] args) {
        new SimpleRotatingCube().run();
    }

    @Override
    public void run() {
        // System.setProperty("ud.apitrace",
        // "/home/octy/Documents/GitHub/apitrace/build/wrappers/glxtrace.so");
        Event.BUS.register(this);

        window = WindowSettings.builder()
                .screenSize(800, 600)
                .title("SimpleRotatingCube")
                .build().createWindow();
        GraphicsContext ctx = window.getGraphicsContext();

        ctx.makeActiveContext();
        window.setVsyncOn(true);
        window.setVisible(true);

        Vector2i size = window.getSize();
        resize(WindowResizeEvent.create(window, size.getX(), size.getY()));

        ColorTextureLoader loader = StandardTextureLoaders.RGBA_COLOR_LOADER;

        TextureData red = loader.load(ColorTextureSpec.create("F00", 1, 1));
        TextureData green = loader.load(ColorTextureSpec.create("0F0", 1, 1));
        TextureData blue = loader.load(ColorTextureSpec.create("00F", 1, 1));
        TextureData vinyl1 = loader.load(ColorTextureSpec.create("3366CC", 1, 1));
        TextureData vinyl2 = loader.load(ColorTextureSpec.create("FEFDE7", 1, 1));
        TextureData vinyl3 = loader.load(ColorTextureSpec.create("18E7E7", 1, 1));

        // setup map...
        TextureCollection base = TextureCollection.of();
        base.put(red, "red");
        base.put(green, "green");
        base.put(blue, "blue");
        base.put(vinyl1, "vinyl1");
        base.put(vinyl2, "vinyl2");
        base.put(vinyl3, "vinyl3");
        // gen atlas
        TextureAtlas atlas = TextureAtlas.create(256, 256, base);

        TextureSettings settings = TextureSettings.builder()
                .downscaling(Downscaling.NEAREST)
                .upscaling(Upscaling.NEAREST)
                .textureWrapping(TextureWrap.CLAMP_TO_EDGE)
                .build();

        // load atlas into card
        Texture texture = ctx.getTextureProvider().load(atlas.getData(), settings);

        // load secondary atlases
        List<Texture> textures = getColors(settings).stream()
                .map(t -> ctx.getTextureProvider().load(t.getData(), settings))
                .collect(toImmutableList());

        List<Vector2d> cubeLayout =
                new CubeLayout(atlas, base)
                        .front("red")
                        .back("green")
                        .left("blue")
                        .right("vinyl1")
                        .top("vinyl2")
                        .bottom("vinyl3")
                        .build();

        Shape shape = ctx.getShapes().rectPrism().shape(
                new Vector3d(-100, -100, -100),
                new Vector3d(100, 100, 100),
                cubeLayout);
        shape.initialize();

        // BindableDrawableSequence texturedShape =
        // BindableDrawableSequence.of(ImmutableList.of(texture),
        // ImmutableList.of(shape));

        window.getMouse().activateMouseGrab();

        double p = 60;
        double y = 60;
        double r = 0;

        while (!window.isCloseRequested()) {
            window.processEvents();
            ctx.clearGraphicsState();

            y = cursorTotalDx.getX() / 7;
            p = -cursorTotalDx.getY() / 7;

            Matrix4f model = Matrix4f.createRotation(Quaternionf.fromAxesAnglesDeg(p, y, r))
                    .translate(300, 300, 0);
            Matrix4f view = Matrix4f.IDENTITY;

            setMVP(ctx, model, view);
            drawShapeWithRandomTexture(textures, shape);

            ctx.swapBuffers();
        }

        window.destroy();
    }

    public void setMVP(GraphicsContext ctx, Matrix4f model, Matrix4f view) {
        Matrix4f mvp = Matrices.buildMVPMatrix(model, view, proj);
        ctx.getMatrixUploader().upload(mvp);
    }

    public void drawShapeWithRandomTexture(List<Texture> textures, Shape shape) {
        Texture t = textures.get(random.nextInt(textures.size()));
        drawShape(shape, t);
    }

    public void drawShape(Shape shape, Texture t) {
        try (Bindable bound = t.bind()) {
            shape.draw();
        }
    }

    private List<TextureAtlas> getColors(TextureSettings textureSettings) {
        return IntStream.range(0, 255).mapToObj(i -> generateTextureCollection(textureSettings)).collect(toImmutableList());
    }

    private TextureAtlas generateTextureCollection(TextureSettings textureSettings) {
        TextureCollection coll = TextureCollection.of();
        for (int i = 0; i < 6; i++) {
            String hex = generateHex();
            coll.put(StandardTextureLoaders.RGBA_COLOR_LOADER.load(
                    ColorTextureSpec.create(hex, 1, 1)), hex);
        }
        return TextureAtlas.create(512, 512, coll);
    }

    private static final Random random = new Random();

    private String generateHex() {
        return String.format("%06x", random.nextInt() & 0xFFFFFF);
    }

    @Subscribe
    public void mouseMove(MouseMoveEvent event) {
        cursorTotalDx = event.getPosition().toFloat();
    }

    @Subscribe
    public void resize(WindowResizeEvent event) {
        Vector2i size = event.getSize();
        proj = Matrices.orthographicProjection(size.getX(), size.getY(), -1000, 1000);
    }

    @Subscribe
    public void keyHandler(KeyStateEvent event) {
        if (event.is(Key.ESCAPE, KeyState.PRESSED)) {
            window.setCloseRequested(true);
        }
    }

}
