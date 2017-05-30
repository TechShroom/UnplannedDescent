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
package com.techshroom.unplanned.tools;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Modifier;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator tool for the KeyModifier enum values
 */
public final class GenerateKeyModifierEnum {

    private static final String PACKAGE = "com.techshroom.unplanned.input";

    private GenerateKeyModifierEnum() {
    }

    /**
     * Key pattern: start of string, class name (%s), "_MOD_", mod name, end of
     * string.
     */
    private static final String NAME_PATTERN = "^%s_MOD_(.+)$";
    /**
     * Javadoc for the entire class.
     */
    private static final String ENUM_JAVADOC =
            "KeyModifier enum values.\n";
    /**
     * Specialized javadoc for each constant.
     */
    private static final String ENUM_CONSTANT_JAVADOC =
            "The value corresponding to the modifier '%s'.\n";
    private static final String SK_WIKIPEDIA = "https://en.wikipedia.org/wiki/Super_key_(keyboard_button)";
    private static final Map<String, String> ENUM_CONSTANT_TO_JAVADOC =
            ImmutableMap.of("SUPER",
                    String.format(ENUM_CONSTANT_JAVADOC, "SUPER")
                            + String.format("\n@see <a href=\"%1$s\">%1$s</a>\n", SK_WIKIPEDIA));

    /**
     * Very limited conversion from key names to Javadoc. Special Javadocs can
     * be configured by inserting them into the map.
     */
    private static String keysToJavadoc(String input) {
        if (ENUM_CONSTANT_TO_JAVADOC.containsKey(input)) {
            return ENUM_CONSTANT_TO_JAVADOC.get(input);
        }
        String validated = input;
        StringBuilder val = new StringBuilder(validated.length());
        char[] chars = validated.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (i != 0 && chars[i - 1] != '_' && ch != '_') {
                val.append(Character.toLowerCase(ch));
            } else if (ch == '_') {
                val.append(' ');
            } else {
                val.append(ch);
            }
        }
        validated = val.toString();
        return String.format(ENUM_CONSTANT_JAVADOC, validated);
    }

    /**
     * I'm going to go ahead and assume you're in the project directory, okay?
     * That's /UnplannedDescent/UDAPI.
     * 
     * @param args
     *            - The arguments
     */
    public static void main(String[] args) {
        List<List<String>> data = collectKeyConstants(GLFW.class);
        List<String> keyNames = ImmutableList.copyOf(data.get(1));
        TypeSpec.Builder spec =
                TypeSpec.enumBuilder("KeyModifier").addModifiers(Modifier.PUBLIC);
        spec.addJavadoc(ENUM_JAVADOC);
        for (int i = 0; i < keyNames.size(); i++) {
            String name = keyNames.get(i);
            TypeSpec.Builder enumVal = TypeSpec.anonymousClassBuilder("");
            enumVal.addJavadoc(keysToJavadoc(name));
            spec.addEnumConstant(name, enumVal.build());
        }
        try {
            JavaFile.builder(PACKAGE, spec.build()).skipJavaLangImports(true)
                    .indent("    ").build()
                    .writeTo(Paths.get("../UDAPI/src/main/java"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Collects any variable names matching {@code <class name>_KEY_<key name>}
     * as the key name.
     * 
     * <p>
     * For example, passing {@code GLFW.class} as the class would hit the
     * {@code GLFW_KEY_A} constant, and the returned array would contain an "A"
     * in it.
     * </p>
     * 
     * @param target
     *            - The class to extract key constants from
     * @return An array of [key constants variable list, key names list]
     */
    private static List<List<String>> collectKeyConstants(Class<?> target) {
        List<Field> constants = extractConstants(target);
        List<String> keyNames = new ArrayList<>();
        List<String> varNames = new ArrayList<>();
        Pattern pat = generateKeyNameMatcherForClass(target);
        for (Field field : constants) {
            Matcher m = pat.matcher(field.getName());
            if (m.matches()) {
                keyNames.add(m.group(1));
                varNames.add(field.getName());
            }
        }
        return ImmutableList.of(varNames, keyNames);
    }

    private static Pattern generateKeyNameMatcherForClass(Class<?> target) {
        return Pattern.compile(
                String.format(NAME_PATTERN, target.getSimpleName()));
    }

    /**
     * Returns only {@code public static final} fields from the given class.
     * 
     * @param target
     *            - The class to extract constants from
     * @return All {@code public static final} fields from the given class
     */
    private static List<Field> extractConstants(Class<?> target) {
        Field[] fields = target.getFields();
        List<Field> constants = Lists.newArrayList(fields);
        for (Field field : fields) {
            int mods = field.getModifiers();
            // constants are public static final fields
            if (!isPublic(mods) || !isStatic(mods) || !isFinal(mods)) {
                // not constant field
                constants.remove(field);
            }
        }
        return constants;
    }
}
