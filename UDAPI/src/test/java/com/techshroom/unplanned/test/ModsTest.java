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
package com.techshroom.unplanned.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.core.mod.Mod;
import com.techshroom.unplanned.core.mod.ModIDConflictException;
import com.techshroom.unplanned.core.mod.ModMetadata;
import com.techshroom.unplanned.core.mod.ModProvider;
import com.techshroom.unplanned.core.mod.Mods;
import com.techshroom.unplanned.core.mod.SkeletalMod;
import com.techshroom.unplanned.core.util.Logging;
import com.techshroom.unplanned.core.util.Logging.LoggingGroup;

public class ModsTest {

    private static final Path SERVICE;

    static {
        Path tmp = null;
        try {
            tmp = Paths
                    .get(Mods.class.getProtectionDomain().getCodeSource()
                            .getLocation().toURI())
                    .resolve(
                            "META-INF/services/" + ModProvider.class.getName());
            Files.createDirectories(tmp.getParent());
            Files.createFile(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SERVICE = tmp;
    }

    private static Set<LoggingGroup> previousLoggingGroup;

    @BeforeClass
    public static void logAllGroups() {
        previousLoggingGroup = Logging.getValidGroups();
        Logging.setValidGroups(LoggingGroup.ALL);
    }

    @AfterClass
    public static void restorePreviousLoggingGroups() {
        Logging.setValidGroups(previousLoggingGroup);
    }

    @Test
    public void testLoadMod() throws Exception {
        Files.write(SERVICE, ImmutableList.of(TestModProvider.class.getName()));
        Mods.findAndLoad();
        List<Mod> mods = Mods.getLoadedMods();
        assertEquals(1, mods.size());
        assertTrue(mods.get(0) instanceof TestMod);
    }

    @Test(expected = ModIDConflictException.class)
    public void testDuplicateMod() throws Exception {
        Files.write(SERVICE, ImmutableList.of(TestModProvider.class.getName(),
                TestDuplicateModProvider.class.getName()));
        Mods.findAndLoad();
    }

    public static class TestModProvider implements ModProvider {

        @Override
        public ModMetadata getMetadata() {
            return ModMetadata.builder().version("1.0.0").buildNumber(0)
                    .idAndName("test").targetGameVersion("1.0.0").build();
        }

        @Override
        public Callable<Mod> getModCreator() {
            return () -> new TestMod(getMetadata());
        }

    }

    public static class TestDuplicateModProvider implements ModProvider {

        @Override
        public ModMetadata getMetadata() {
            return ModMetadata.builder().version("1.0.0").buildNumber(0)
                    .idAndName("test").targetGameVersion("1.0.0").build();
        }

        @Override
        public Callable<Mod> getModCreator() {
            return () -> new TestMod(getMetadata());
        }

    }

    public static class TestMod extends SkeletalMod {

        protected TestMod(ModMetadata data) {
            super(data);
        }

    }

}
