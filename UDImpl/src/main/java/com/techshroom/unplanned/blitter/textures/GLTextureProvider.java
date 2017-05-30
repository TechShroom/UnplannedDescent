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
package com.techshroom.unplanned.blitter.textures;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

import java.nio.IntBuffer;

import com.google.auto.service.AutoService;
import com.techshroom.unplanned.core.util.GLErrorCheck;

@AutoService(TextureProvider.class)
public class GLTextureProvider implements TextureProvider {

    @Override
    public Texture load(TextureData data, TextureSettings settings) {
        GLErrorCheck.preflightCheck();
        // create new texture
        GLTexture texture = new GLTexture(glGenTextures(), data.getWidth(), data.getHeight());

        try (GLTexture bound = texture.bind()) {
            // upload data to card
            IntBuffer pixels = data.getDataAsSingleArray();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, data.getWidth(), data.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, getMagFilter(settings.getUpscaling()));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, getMinFilter(settings.getDownscaling()));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, getTextureWrap(settings.getTextureWrapping()));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, getTextureWrap(settings.getTextureWrapping()));

            GLErrorCheck.check();
            return texture;
        }
    }

    private int getMagFilter(Upscaling upscaling) {
        switch (upscaling) {
            case LINEAR:
                return GL_LINEAR;
            case NEAREST:
                return GL_NEAREST;
            default:
                throw new IllegalArgumentException(upscaling.toString());
        }
    }

    private int getMinFilter(Downscaling downscaling) {
        switch (downscaling) {
            case LINEAR:
                return GL_LINEAR;
            case LINEAR_FROM_NEAREST_MIPMAP:
                return GL_LINEAR_MIPMAP_NEAREST;
            case LINEAR_FROM_NEAREST_TWO_MIPMAPS:
                return GL_LINEAR_MIPMAP_LINEAR;
            case NEAREST:
                return GL_NEAREST;
            case NEAREST_FROM_NEAREST_MIPMAP:
                return GL_NEAREST_MIPMAP_NEAREST;
            case NEAREST_FROM_NEAREST_TWO_MIPMAPS:
                return GL_NEAREST_MIPMAP_LINEAR;
            default:
                throw new IllegalArgumentException(downscaling.toString());
        }
    }

    private int getTextureWrap(TextureWrap textureWrapping) {
        switch (textureWrapping) {
            case CLAMP_TO_EDGE:
                return GL_CLAMP_TO_BORDER;
            case MIRRORED_REPEAT:
                return GL_MIRRORED_REPEAT;
            case REPEAT:
                return GL_REPEAT;
            default:
                throw new IllegalArgumentException(textureWrapping.toString());
        }
    }

}
