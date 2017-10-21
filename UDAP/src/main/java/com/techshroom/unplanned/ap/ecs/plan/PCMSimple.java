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
package com.techshroom.unplanned.ap.ecs.plan;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;
import java.util.function.UnaryOperator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;

import com.google.auto.common.MoreTypes;
import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.techshroom.unplanned.core.util.UDStrings;

public final class PCMSimple implements PlanCodeManager {

    private static String fieldName(String compName, String fieldName) {
        return compName + "_" + fieldName;
    }

    private static String setterName(String compName, String fieldName) {
        return compName + UDStrings.uppercaseFirstLetter(fieldName);
    }

    private final ProcessingEnvironment env;

    public PCMSimple(ProcessingEnvironment env) {
        this.env = env;
    }

    @Override
    public List<FieldSpec> generateFields(PlanComponent component) {
        return component.getFields().stream().map(compField -> {
            // compfield is like ComponentField<XYZ> nameOfField;
            DeclaredType declType = MoreTypes.asDeclared(compField.asType());
            // we pull out the XYZ and the nameOfField
            return FieldSpec.builder(
                    TypeName.get(declType.getTypeArguments().get(0)),
                    fieldName(component.getName(), compField.getSimpleName().toString()),
                    Modifier.PRIVATE).build();
        }).collect(toImmutableList());
    }

    @Override
    public List<MethodSpec> generateBuilderSetters(UnaryOperator<MethodSpec.Builder> config, PlanComponent component) {
        return component.getFields().stream().map(compField -> {
            // compfield is like ComponentField<XYZ> nameOfField;
            DeclaredType declType = MoreTypes.asDeclared(compField.asType());
            // we pull out the XYZ and the nameOfField
            // then we merge it to create a name like $COMP_NAME$NAME_OF_FIELD
            // ex: gridPositionX
            String nameOfField = compField.getSimpleName().toString();
            String name = setterName(component.getName(), nameOfField);
            return config.apply(MethodSpec.methodBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.get(declType.getTypeArguments().get(0)), nameOfField)
                    .addStatement("this.$L = $L", fieldName(component.getName(), nameOfField), nameOfField)).build();
        }).collect(toImmutableList());
    }

    @Override
    public CodeBlock generateAssignment(String entityArg, String assocArg, TypeName source, PlanComponent component) {
        CodeBlock.Builder cb = CodeBlock.builder();
        component.getFields().forEach(compField -> {
            String fieldName = fieldName(component.getName(), compField.getSimpleName().toString());
            cb.beginControlFlow("if ($L != null)", fieldName);
            cb.addNamed("$assoc:L.set($entity:L, $source:T.$comp:L().getField($name:S), this.$value:L);\n", new ImmutableMap.Builder<String, Object>()
                    .put("assoc", assocArg)
                    .put("entity", entityArg)
                    .put("source", source)
                    .put("comp", component.getName())
                    .put("name", compField.getSimpleName().toString())
                    .put("value", fieldName)
                    .build());
            cb.endControlFlow();
        });
        return cb.build();
    }
}
