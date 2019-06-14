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

package com.techshroom.unplanned.ap.ecs.plan;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.techshroom.unplanned.ecs.CompEntAssoc;

public class PlanGenerator {

    private static final String BUILDER_NAME = "Builder";

    public static String getBuilderName() {
        return BUILDER_NAME;
    }

    private final TypeName source;
    private final List<PlanComponent> components;

    public PlanGenerator(TypeName source, List<PlanComponent> components) {
        this.source = source;
        this.components = components;
    }

    public JavaFile generate(ClassName className, @Nullable ClassName extension) {
        TypeSpec.Builder spec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC);

        if (extension != null) {
            spec.superclass(extension);
        }

        // starter method
        spec.addMethod(MethodSpec.methodBuilder("start")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(className)
                .addStatement("return new $T()", className)
                .build());

        // fields
        spec.addFields(generateFields());

        // private X() {}
        spec.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

        // setters
        spec.addMethods(generateSetters(className));

        // public int build(CEA assoc) { ... }
        spec.addMethod(MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CompEntAssoc.class, "assoc")
                .addCode(generateBuildCode())
                .returns(int.class)
                .build());

        return JavaFile.builder(className.packageName(), spec.build())
                .addFileComment("Generated on $L by $L.",
                        DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                        getClass().getName())
                .indent("    ")
                .build();
    }

    private Iterable<FieldSpec> generateFields() {
        return components.stream()
                .flatMap(pc -> pc.getCodeManager().generateFields(pc).stream())
                .collect(toImmutableList());
    }

    private Iterable<MethodSpec> generateSetters(ClassName className) {
        return components.stream()
                .flatMap(pc -> pc.getCodeManager().generateBuilderSetters(metSpec -> {
                    return metSpec.returns(className)
                            .addStatement("return this");
                }, pc).stream())
                .collect(toImmutableList());
    }

    private CodeBlock generateBuildCode() {
        CodeBlock.Builder cb = CodeBlock.builder();

        // get a new entity made of our components
        cb.addStatement("int e = assoc.newEntity($L)", getComponentArgList());

        // run through all assignments
        for (PlanComponent c : components) {
            cb.add(c.getCodeManager().generateAssignment("e", "assoc", source, c));
            cb.add("\n");
        }

        // return entity
        cb.addStatement("return e");

        return cb.build();
    }

    private CodeBlock getComponentArgList() {
        CodeBlock.Builder cb = CodeBlock.builder();
        for (Iterator<PlanComponent> iterator = components.iterator(); iterator.hasNext();) {
            PlanComponent c = iterator.next();
            cb.add("$T.$L()", source, c.getName());
            if (iterator.hasNext()) {
                cb.add(",\n");
            }
        }
        return cb.build();
    }

}
