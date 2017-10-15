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
package com.techshroom.unplanned.examples.rp;

import java.nio.file.Paths;
import java.util.Locale;

import com.techshroom.unplanned.app.App;
import com.techshroom.unplanned.core.util.CombinedId;
import com.techshroom.unplanned.rp.LangResource.Translator;
import com.techshroom.unplanned.rp.ResourceType;

/**
 * Example of loading resource packs.
 */
public class ResourcePackExample {

    private final App APP = App.builder()
            .id(CombinedId.fromTitle("UnplannedDescentResourcePackExample"))
            .baseDir(Paths.get("./rp-ex"))
            .build();

    public void main() {
        Translator translator = APP.getResourcePack().loadResource(APP.rId("lang/app"), ResourceType.LANG)
                .translator(Locale.getDefault());
        System.err.println(translator.tr("hello", APP.getName()));
    }

    public static void main(String[] args) {
        new ResourcePackExample().main();
    }

}
