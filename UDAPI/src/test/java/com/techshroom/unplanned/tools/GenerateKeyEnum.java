package com.techshroom.unplanned.tools;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Modifier;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator tool for the Key enum values
 * 
 * @author Kenzie Togami
 */
public final class GenerateKeyEnum {

    private static final String PACKAGE = "com.techshroom.unplanned.keyboard";

    private GenerateKeyEnum() {
    }

    /**
     * Key pattern: start of string, class name (%s), "_KEY_", key name, end of
     * string.
     */
    private static final String KEY_NAME_PATTERN = "^%s_KEY_(.+)$";
    /**
     * Javadoc for the entire class.
     */
    private static final String KEY_ENUM_JAVADOC = ""
            + "Key enum values for the {@link GLFW} class." + "\n\n"
            + "@author Kenzie Togami\n";
    /**
     * Specialized javadoc for each constant.
     */
    private static final String KEY_ENUM_CONSTANT_JAVADOC = ""
            + "The value corresponding to the key '%s'.\n";
    private static final String GET_GLFWCODE_JAVADOC = ""
            + "Gets the corresponding {@code GLFW int code} for the key."
            + "\n\n"
            + "@return The corresponding {@code GLFW int code} for the key\n";
    private static final ImmutableMap<String, String> KEY_ENUM_CONSTANT_TO_JAVADOC =
            ImmutableMap
                    .<String, String> builder()
                    .put("UNKNOWN",
                            "The value corresponding to an unknown key.")
                    .build();

    /**
     * Very limited conversion from key names to field names. Pretty much only
     * works on numbers.
     */
    private static String convertKeyName(String input) {
        String validated = input;
        if (Character.isDigit(input.charAt(0))) {
            validated = "NUM_" + input;
        }
        return validated;
    }

    /**
     * Very limited conversion from key names to Javadoc. Special Javadocs can
     * be configured by inserting them into the map.
     */
    private static String keysToJavadoc(String input) {
        if (KEY_ENUM_CONSTANT_TO_JAVADOC.containsKey(input)) {
            return KEY_ENUM_CONSTANT_TO_JAVADOC.get(input);
        }
        String validated = input;
        if (input.startsWith("NUM_")) {
            validated = input.substring(4);
        } else if (input.startsWith("KP_")) {
            validated = input.replaceFirst("KP_", "Keypad ");
        } else if (input.startsWith("LEFT_")) {
            validated = input.replaceFirst("LEFT_", "Left ");
        } else if (input.startsWith("RIGHT_")) {
            validated = input.replaceFirst("RIGHT_", "Right ");
        }
        return String.format(KEY_ENUM_CONSTANT_JAVADOC, validated);
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
        List<String> keyNames =
                ImmutableList.copyOf(data.get(1).stream()
                        .map(GenerateKeyEnum::convertKeyName)::iterator);
        TypeSpec.Builder spec =
                TypeSpec.enumBuilder("Key").addModifiers(Modifier.PUBLIC);
        spec.addJavadoc(KEY_ENUM_JAVADOC);
        for (int i = 0; i < keyNames.size(); i++) {
            String name = keyNames.get(i);
            System.err.println("Inserting key enum value \"" + name + "\"");
            TypeSpec.Builder enumVal =
                    TypeSpec.anonymousClassBuilder("$T.$L", GLFW.class, data
                            .get(0).get(i));
            // enumVal.addJavadoc(keysToJavadoc(name));
            spec.addEnumConstant(name, enumVal.build());
        }
        spec.addField(int.class, "glfwCode", Modifier.PRIVATE, Modifier.FINAL);
        spec.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(int.class, "glfwCode")
                .addStatement("this.$L = $L", "glfwCode", "glfwCode").build());
        spec.addMethod(MethodSpec.methodBuilder("getGLFWCode")
                .addJavadoc(GET_GLFWCODE_JAVADOC).addModifiers(Modifier.PUBLIC)
                .returns(int.class).addStatement("return this.$L", "glfwCode")
                .build());
        spec.addMethod(MethodSpec
                .methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return $S + name().replaceFirst($S, $S)",
                        "GLFW_KEY_", "NUM_(\\d)", "\\1").build());
        try {
            JavaFile.builder(PACKAGE, spec.build()).skipJavaLangImports(true)
                    .indent("    ").build().writeTo(Paths.get("src/main/java"));
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
        return Pattern.compile(String.format(KEY_NAME_PATTERN,
                target.getSimpleName()));
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
