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

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.tools.Diagnostic.Kind;

import com.google.auto.common.MoreTypes;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.techshroom.unplanned.ecs.ComplexComponent;

public final class PCMComplex implements PlanCodeManager {

    private static String useComplexFieldName(String compName) {
        return fieldName(compName, "complexValueSet");
    }

    private static String fieldName(String compName, String fieldName) {
        return compName + "_" + fieldName;
    }

    private final PlanCodeManager simpleDelegate;
    private final ProcessingEnvironment env;

    public PCMComplex(ProcessingEnvironment env) {
        this.env = env;
        this.simpleDelegate = new PCMSimple(env);
    }

    private TypeName extractCCGeneric(TypeMirror typeMirror) {
        TypeMirror ccMirror = env.getTypeUtils().erasure(
                env.getElementUtils().getTypeElement(ComplexComponent.class.getCanonicalName())
                        .asType());
        // pull out the ComplexComponent subclass
        TypeElement clazz = MoreTypes.asTypeElement(typeMirror);
        List<TypeElement> candidates = new ArrayList<>();
        candidates.add(clazz);
        while (true) {
            TypeElement top = Iterables.getLast(candidates);
            TypeMirror sup = top.getSuperclass();
            if (env.getTypeUtils().isSameType(ccMirror, env.getTypeUtils().erasure(sup))) {
                break;
            }
            candidates.add(MoreTypes.asTypeElement(sup));
        }
        int ccGenericIndex = 0;
        for (TypeElement potHolder : Lists.reverse(candidates)) {
            TypeMirror sup = potHolder.getSuperclass();
            DeclaredType declaredSup = MoreTypes.asDeclared(sup);
            TypeMirror typeArg = declaredSup.getTypeArguments().get(ccGenericIndex);
            switch (typeArg.getKind()) {
                case DECLARED:
                    // we found the real generic!
                    return TypeName.get(typeArg);
                case TYPEVAR:
                    // we found a type variable -- find the index and look in
                    // subclass
                    TypeVariable tvar = MoreTypes.asTypeVariable(typeArg);
                    Stream<Long> indexSearch = Streams.mapWithIndex(potHolder.getTypeParameters()
                            .stream()
                            .map(el -> el.getSimpleName()),
                            (name, i) -> name.contentEquals(tvar.asElement().getSimpleName()) ? i : -1);
                    ccGenericIndex = (int) indexSearch
                            .mapToLong(Long::longValue)
                            .filter(l -> l != -1).findFirst().getAsLong();
                    break;
                default:
                    // I don't know what to do in this case -- can it happen?
                    throw new AssertionError("Unknown case " + typeArg.getKind());
            }
        }
        env.getMessager().printMessage(Kind.WARNING,
                "No generic found for " + typeMirror + ", using Object", MoreTypes.asElement(typeMirror));
        return TypeName.OBJECT;
    }

    private boolean needSimpleParts(PlanComponent component) {
        int numOfFields = component.getFields().size();
        if (numOfFields == 1) {
            TypeName complexType = extractCCGeneric(component.getComponent());
            DeclaredType declType = MoreTypes.asDeclared(component.getFields().get(0).asType());
            TypeName typeOfFields = TypeName.get(declType.getTypeArguments().get(0));
            if (complexType.equals(typeOfFields)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<FieldSpec> generateFields(PlanComponent component) {
        ImmutableList.Builder<FieldSpec> b = ImmutableList.builder();
        // complex-in-use field
        b.add(FieldSpec.builder(boolean.class, useComplexFieldName(component.getName()), Modifier.PRIVATE)
                .build());
        // complex field
        b.add(FieldSpec.builder(extractCCGeneric(component.getComponent()), fieldName(component.getName(), "complex"), Modifier.PRIVATE)
                .build());
        if (needSimpleParts(component)) {
            b.addAll(simpleDelegate.generateFields(component));
        }
        return b.build();
    }

    @Override
    public List<MethodSpec> generateBuilderSetters(UnaryOperator<MethodSpec.Builder> config, PlanComponent component) {
        ImmutableList.Builder<MethodSpec> b = ImmutableList.builder();
        b.add(complexSetter(config, component));
        if (needSimpleParts(component)) {
            b.addAll(simpleDelegate.generateBuilderSetters(config, component));
        }
        return b.build();
    }

    private MethodSpec complexSetter(UnaryOperator<MethodSpec.Builder> config, PlanComponent component) {
        String backingFieldName = fieldName(component.getName(), "complex");
        return config.apply(MethodSpec.methodBuilder(component.getName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(extractCCGeneric(component.getComponent()), backingFieldName)
                .addCode(CodeBlock.builder()
                        .addStatement("this.$L = true", useComplexFieldName(component.getName()))
                        .addStatement("this.$1L = $1L", backingFieldName)
                        .build()))
                .build();
    }

    @Override
    public CodeBlock generateAssignment(String entityArg, String assocArg, TypeName source, PlanComponent component) {
        CodeBlock.Builder cb = CodeBlock.builder();
        cb.beginControlFlow("if ($L)", useComplexFieldName(component.getName()));
        cb.addNamed("$source:T.$field:L().set($assoc:L, $entity:L, this.$value:L);\n", ImmutableMap.of(
                "assoc", assocArg,
                "entity", entityArg,
                "source", source,
                "field", component.getName(),
                "value", fieldName(component.getName(), "complex")));
        if (needSimpleParts(component)) {
            cb.nextControlFlow("else");
            cb.add(simpleDelegate.generateAssignment(entityArg, assocArg, source, component));
        }
        cb.endControlFlow();
        return cb.build();
    }
}
