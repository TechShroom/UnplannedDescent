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
package com.techshroom.unplanned.baleout;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.ValueConverter;

public class BaleOut {

    private static final class FrontendConverter implements ValueConverter<Frontend> {

        private static final Map<String, Frontend> FRONTENDS = Streams.stream(ServiceLoader.load(Frontend.class))
                .collect(toImmutableMap(Frontend::getId, Function.identity()));

        @Override
        public Frontend convert(String value) {
            return FRONTENDS.get(value);
        }

        @Override
        public Class<? extends Frontend> valueType() {
            return Frontend.class;
        }

        @Override
        public String valuePattern() {
            return String.join("|", FRONTENDS.keySet());
        }

    }

    private static final OptionParser PARSER = new OptionParser();

    private static final ArgumentAcceptingOptionSpec<Frontend> FRONTEND =
            PARSER.accepts("frontend", "Frontend to use")
                    .withRequiredArg()
                    .withValuesConvertedBy(new FrontendConverter())
                    .defaultsTo(new ConsoleFrontend());

    private static final OptionSpec<String> SUBPARSER_ARGS = PARSER.nonOptions();

    public static void main(String[] args) throws Exception {
        PARSER.allowsUnrecognizedOptions();
        Optional<OptionSet> optsional = JOptCommon.parse(PARSER, args);

        if (!optsional.isPresent()) {
            return;
        }

        OptionSet opts = optsional.get();

        opts.valueOf(FRONTEND).run(Iterables.toArray(opts.valuesOf(SUBPARSER_ARGS), String.class));
    }

}
