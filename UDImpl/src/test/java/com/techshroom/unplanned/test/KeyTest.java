package com.techshroom.unplanned.test;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.keyboard.Key;

/**
 * Testing for the {@link Key} class.
 * 
 * @author Kenzie Togami
 */
public class KeyTest {

    /**
     * Ensure that the translation works backwards from
     * {@code Key -> GLFW constant}.
     */
    @Test
    public void ensureKeyNamesExist() {
        List<String> keyNames = grabKeyConstantNames();
        for (String constant : keyNames) {
            Object constVal = null;
            try {
                constVal = GLFW.class.getDeclaredField(constant);
            } catch (NoSuchFieldException | SecurityException e) {
                // rip.
            }
            assertNotNull("no such key constant " + constant, constVal);
        }
    }

    private List<String> grabKeyConstantNames() {
        return ImmutableList.copyOf(
                Stream.of(Key.values()).map(Key::getGLFWName)::iterator);
    }
}
