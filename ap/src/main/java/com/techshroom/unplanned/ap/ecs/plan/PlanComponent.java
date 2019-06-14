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

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreElements;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.techshroom.unplanned.ecs.ComplexComponent;
import com.techshroom.unplanned.ecs.ComponentField;

@AutoValue
public abstract class PlanComponent {

    public static PlanComponent from(ProcessingEnvironment env, TypeMirror component, String name) {
        return new AutoValue_PlanComponent(env, component, name);
    }

    abstract ProcessingEnvironment getEnv();

    public abstract TypeMirror getComponent();

    public abstract String getName();

    @Memoized
    public List<VariableElement> getFields() {
        TypeMirror basicCompFieldType = getEnv().getElementUtils().getTypeElement(ComponentField.class.getCanonicalName()).asType();
        TypeMirror compFieldType = getEnv().getTypeUtils().erasure(basicCompFieldType);
        return getEnv().getTypeUtils().asElement(getComponent()).getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(f -> MoreElements.asVariable(f))
                .filter(f -> {
                    TypeMirror typeOfField = f.asType();
                    return getEnv().getTypeUtils().isAssignable(typeOfField, compFieldType);
                })
                .collect(toImmutableList());
    }

    @Memoized
    public PlanCodeManager getCodeManager() {
        TypeMirror compComponent = getEnv().getElementUtils().getTypeElement(ComplexComponent.class.getCanonicalName()).asType();
        compComponent = getEnv().getTypeUtils().erasure(compComponent);
        return getEnv().getTypeUtils().isAssignable(getComponent(), compComponent)
                ? new PCMComplex(getEnv())
                : new PCMSimple(getEnv());
    }

}
