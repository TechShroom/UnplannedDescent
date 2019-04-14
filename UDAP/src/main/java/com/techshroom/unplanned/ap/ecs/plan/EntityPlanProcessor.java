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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.techshroom.unplanned.ap.AbortProcessingException;
import com.techshroom.unplanned.ap.MissingTypeException;
import com.techshroom.unplanned.ecs.Component;

@AutoService(Processor.class)
public class EntityPlanProcessor extends BasicAnnotationProcessor {

    private static final class GenerationStep implements ProcessingStep {

        private static final Set<Modifier> PUBLIC_STATIC = Sets.immutableEnumSet(Modifier.PUBLIC, Modifier.STATIC);
        private final ProcessingEnvironment env;

        public GenerationStep(ProcessingEnvironment env) {
            this.env = env;
        }

        @Override
        public Set<? extends Class<? extends Annotation>> annotations() {
            return ImmutableSet.of(EntityPlan.class);
        }

        @Override
        public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
            ImmutableSet.Builder<Element> unproc = ImmutableSet.builder();
            for (Element e : elementsByAnnotation.get(EntityPlan.class)) {
                try {
                    process(e);
                } catch (AbortProcessingException | MissingTypeException ex) {
                    unproc.add(e);
                }
            }
            return unproc.build();
        }

        private void process(Element e) {
            ClassName name = (ClassName) TypeName.get(e.asType());
            List<PlanComponent> comps = loadComponents(e);
            ClassName className = name.peerClass(name.simpleName() + "Plan");
            JavaFile file = new PlanGenerator(name, comps)
                    .generate(className, null);
            try {
                file.writeTo(env.getFiler());
            } catch (IOException ex) {
                env.getMessager()
                        .printMessage(Kind.ERROR, "Error writing generated class: " + ex.getMessage(), e);
                throw new AbortProcessingException();
            }
        }

        private List<PlanComponent> loadComponents(Element x) {
            TypeMirror componentType = env.getTypeUtils().erasure(
                    env.getElementUtils().getTypeElement(Component.class.getCanonicalName())
                            .asType());
            TypeElement clazz = MoreElements.asType(x);
            List<TypeElement> candidates = new ArrayList<>();
            candidates.add(clazz);
            while (true) {
                TypeElement top = Iterables.getLast(candidates);
                TypeMirror sup = top.getSuperclass();
                if (TypeName.get(sup).equals(TypeName.OBJECT)) {
                    break;
                }
                candidates.add(MoreTypes.asTypeElement(sup));
            }
            return candidates.stream().flatMap(cte -> {
                return cte.getEnclosedElements().stream()
                        .filter(e -> e.getKind() == ElementKind.METHOD)
                        .map(e -> MoreElements.asExecutable(e))
                        .filter(method -> {
                            return method.getModifiers().containsAll(PUBLIC_STATIC);
                        })
                        .filter(method -> {
                            TypeMirror retType = method.getReturnType();
                            if (retType.getKind() == TypeKind.NONE) {
                                return false;
                            }
                            return env.getTypeUtils().isAssignable(
                                    env.getTypeUtils().erasure(retType),
                                    componentType);
                        })
                        .map(method -> PlanComponent.from(env, method.getReturnType(), method.getSimpleName().toString()));
            }).collect(toImmutableList());
        }

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        return ImmutableList.of(new GenerationStep(processingEnv));
    }

}
