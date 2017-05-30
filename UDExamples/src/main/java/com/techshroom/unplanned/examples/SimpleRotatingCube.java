/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshroom.com>
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
import com.flowpowered.math.vector.Vector3f;
import com.google.auto.service.AutoService;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.Shape;
import com.techshroom.unplanned.blitter.Vertex;
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
import com.techshroom.unplanned.input.Keyboard;
import com.techshroom.unplanned.window.Window;
import com.techshroom.unplanned.window.WindowSettings;

@AutoService(Example.class)
public class SimpleRotatingCube extends Example {

    private Window window;
    private Matrix4f proj;
    private Matrix4f view;
    private Vector2f cursorTotalDx = Vector2f.ZERO;
    private double cameraPullback = 1000;
    private Vector2d centerScreen;
    private Vector3f cameraRot = Vector3f.ZERO;
    private List<Vector2d> cubeLayout;
    private Shape shape;
    private Shape lightSource;

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
        Texture redTex = ctx.getTextureProvider().load(red, settings);

        // load secondary atlases
        List<Texture> textures = getColors(settings).stream()
                .map(t -> ctx.getTextureProvider().load(t.getData(), settings))
                .collect(toImmutableList());

        cubeLayout = new CubeLayout(atlas, base)
                .front("red")
                .back("green")
                .left("blue")
                .right("vinyl1")
                .top("vinyl2")
                .bottom("vinyl3")
                .build();

        initShape();

        // somewhere in the center
        centerScreen = size.toDouble().div(2);
        updateViewMatrix();
        ctx.setLight(new Vector3d(centerScreen.add(10000, 0), 10000), new Vector3d(1.0, 1.0, 1.0));

        // BindableDrawableSequence texturedShape =
        // BindableDrawableSequence.of(ImmutableList.of(texture),
        // ImmutableList.of(shape));

        window.getMouse().activateMouseGrab();

        double p = 60;
        double y = 60;
        double r = 0;

        Vector3f translate = Vector3f.ZERO;// centerScreen.toVector3().toFloat();

        while (!window.isCloseRequested()) {
            window.processEvents();
            ctx.clearGraphicsState();

            y = cursorTotalDx.getX() / 7;
            p = (-cursorTotalDx.getY() / 7);

            p += 5;
            y += 3;

            setRotatatedMVP(ctx, translate, new Vector3d(p, y, r));
            drawShape(shape, texture);
            setRotatatedMVP(ctx, translate.add(200, 0, 0), new Vector3d(p, y, r));
            drawShape(shape, texture);
            setRotatatedMVP(ctx, translate.sub(200, 0, 0), new Vector3d(p, y, r));
            drawShape(shape, texture);
            drawShape(shape, texture);
            setRotatatedMVP(ctx, translate.add(0, 200, 0), new Vector3d(p, y, r));
            drawShape(shape, texture);
            setRotatatedMVP(ctx, translate.sub(0, 200, 0), new Vector3d(p, y, r));
            drawShape(shape, texture);

            setRotatatedMVP(ctx, translate, Vector3d.ZERO);
            drawShape(lightSource, redTex);

            handleCamera();

            ctx.swapBuffers();
        }

        window.destroy();
    }

    public void setRotatatedMVP(GraphicsContext ctx, Vector3f translate, Vector3d pyr) {
        Matrix4f model = Matrix4f.createRotation(Quaternionf.fromAxesAnglesDeg(pyr.getX(), pyr.getY(), pyr.getZ()))
                .translate(translate);

        setMVP(ctx, model);
    }

    public void setMVP(GraphicsContext ctx, Matrix4f model) {
        ctx.getMatrixUploader().upload(model, view, proj);
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

    private void initShape() {
        if (shape != null) {
            shape.destroy();
        }
        if (lightSource != null) {
            lightSource.destroy();
        }
        shape = window.getGraphicsContext().getShapes().rectPrism().shape(
                new Vector3d(-100, -100, -100),
                new Vector3d(100, 100, 100),
                cubeLayout);
        shape.initialize();

        lightSource = window.getGraphicsContext().getShapes().triangle().shape(
                Vertex.at(-25, 0, 200).texture(0, 0).build(),
                Vertex.at(25, 0, 200).texture(1, 0).build(),
                Vertex.at(0, 50, 200).texture(0.5, 1).build());
        lightSource.initialize();
    }

    private void updateViewMatrix() {
        Quaternionf cameraRot = Quaternionf.fromAxesAnglesDeg(
                this.cameraRot.getX(),
                this.cameraRot.getY(),
                this.cameraRot.getZ());
        // camera is looking at (0,0,0) by default, not rotated
        // if we want the camera to be at (1000,0,0)
        // (90deg off from (0,0,1000), the default)
        // and looking -90, so it looks towards the center.
        // this should end up rotating the world by 90deg in some dir

        // compute the ACTUAL camera position by pulling back in the dir of the
        // rotation
        Vector3f cameraPos = cameraRot.getDirection().mul(cameraPullback);
        // compute world transform as inverse
        Vector3f worldTranslate = cameraPos.negate();

        // compute ACTUAL rotation as cameraRot
        // compute world transform as inverse (conjugate)
        Quaternionf worldRotation = cameraRot.conjugate().normalize();

        // return combined world transform
        view = Matrix4f.createTranslation(worldTranslate).rotate(worldRotation);
    }

    @Subscribe
    public void mouseMove(MouseMoveEvent event) {
        cursorTotalDx = event.getPosition().toFloat();
    }

    @Subscribe
    public void resize(WindowResizeEvent event) {
        Vector2i size = event.getSize();
        proj = Matrices.orthographicProjection(size.getX(), size.getY(), -10000, 10000);
    }

    @Subscribe
    public void keyHandler(KeyStateEvent event) {
        if (event.is(Key.ESCAPE, KeyState.PRESSED)) {
            window.setCloseRequested(true);
        } else if (event.is(Key.R, KeyState.PRESSED)) {
            initShape();
        }
    }

    private boolean handleCamera() {
        Keyboard kbd = window.getKeyboard();
        final double change = 5;
        boolean any = false;
        if (kbd.isKeyDown(Key.W)) {
            cameraRot = cameraRot.add(change, 0, 0);
            any = true;
        }
        if (kbd.isKeyDown(Key.S)) {
            cameraRot = cameraRot.add(-change, 0, 0);
            any = true;
        }
        if (kbd.isKeyDown(Key.D)) {
            cameraRot = cameraRot.add(0, change, 0);
            any = true;
        }
        if (kbd.isKeyDown(Key.A)) {
            cameraRot = cameraRot.add(0, -change, 0);
            any = true;
        }

        if (any) {
            updateViewMatrix();
        }

        return any;
    }

}
