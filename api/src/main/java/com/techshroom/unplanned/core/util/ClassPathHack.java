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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.collect.ImmutableList;

/**
 * Hack for injecting things into the classpath.
 * 
 * @author Kenzie Togami
 */
public class ClassPathHack {

    private static final Class<?>[] parameters = new Class<?>[] { URL.class };

    /**
     * Adds the file represented by the given String to the classpath.
     * 
     * @param s
     *            - a file, represented as a String
     * @throws IOException
     *             if there are any problems injecting.
     */
    public static void addFile(String s) throws IOException {
        addFile(Paths.get(s));
    }

    /**
     * Adds the given {@link File} to the classpath.
     * 
     * @param f
     *            - a File
     * @throws IOException
     *             if there is any problems injecting.
     */
    public static void addFile(Path f) throws IOException {
        addURL(f.toUri().toURL());
    }

    /**
     * Adds the given {@link URL} to the classpath.
     * 
     * @param u
     *            - a URL
     * @throws IOException
     *             if there is any problems injecting.
     */
    public static void addURL(URL u) throws IOException {
        URLClassLoader sysloader =
                (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] { u });
        } catch (Throwable t) {
            throw new IOException(
                    "Error, could not add URL to system classloader", t);
        }

        System.setProperty("java.class.path",
                System.getProperty("java.class.path") + File.pathSeparator
                        + u.getFile().replace('/', File.separatorChar)
                                .substring(1).replace("%20", " "));
    }

    public static boolean hasFile(String file) {
        return hasFile(Paths.get(file));
    }

    public static boolean hasFile(Path file) {
        try {
            return hasURL(file.toUri().toURL());
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean hasURL(URL u) {
        URLClassLoader sysloader =
                (URLClassLoader) ClassLoader.getSystemClassLoader();
        return ImmutableList.copyOf(sysloader.getURLs()).contains(u);
    }

}