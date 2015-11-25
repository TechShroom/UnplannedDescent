package com.techshroom.unplanned.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

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

    @BeforeClass
    public static void logAllGroups() {
        Logging.setValidGroups(LoggingGroup.ALL);
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
