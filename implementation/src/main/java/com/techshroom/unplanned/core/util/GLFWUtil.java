/*
 * This file is part of unplanned-descent, licensed under the MIT License (MIT).
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

package com.techshroom.unplanned.core.util;

import static com.google.common.base.Preconditions.checkState;
import static org.lwjgl.glfw.GLFW.glfwInit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.system.Configuration;
import org.slf4j.Logger;

import com.techshroom.unplanned.core.Settings;

public final class GLFWUtil {

    private static final Logger LOGGER = Logging.getLogger();

    private static boolean initialized;

    public static void ensureInitialized() {
        if (initialized) {
            return;
        }
        LOGGER.trace("GLFWUtil initializing...");
        initialized = true;
        /* use apitrace if requested */
        String apiTrace = Settings.APITRACE;
        if (!apiTrace.isEmpty()) {
            // check exists
            Path path = Paths.get(apiTrace);
            checkState(Files.exists(path), "apitrace file %s does not exist", apiTrace);
            // set gl library location
            Configuration.OPENGL_LIBRARY_NAME.set(path.toAbsolutePath().toString());
            LOGGER.info("Using APITrace " + apiTrace);
        }
        GLFWErrorHandler.setAsErrorCallback();
        checkState(glfwInit(), "failed to initialize GLFW");
    }

    private GLFWUtil() {
    }

}
