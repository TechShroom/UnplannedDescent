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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.techshroom.unplanned.baleout.MapValueConverter.ValueWrapper;
import com.techshroom.unplanned.baleout.ff2.CalculationOption;
import com.techshroom.unplanned.baleout.ff2.CalculationOptions;
import com.techshroom.unplanned.baleout.ff2.CategoryGroupingFF2Calculator;
import com.techshroom.unplanned.baleout.ff2.FF2Calculator;
import com.techshroom.unplanned.baleout.ff2.FF2ResourcePackWriter;
import com.techshroom.unplanned.core.util.Exit;
import com.techshroom.unplanned.core.util.genericmap.GenericMap;
import com.techshroom.unplanned.core.util.genericmap.GenericMaps;
import com.techshroom.unplanned.rp.RId;
import com.techshroom.unplanned.rp.ff2.FF2Index;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;

/**
 * Non-interactive, default frontend.
 */
@AutoService(Frontend.class)
public class ConsoleFrontend extends BaseFrontend {

    private static final class CalculatorConverter extends MapValueConverter<FF2Calculator> {

        protected CalculatorConverter() {
            super(ImmutableMap.of(
                    "categoryGrouping", new CategoryGroupingFF2Calculator()));
        }

    }

    private static final OptionParser PARSER = new OptionParser();

    private static final Map<CalculationOption<?>, ArgumentAcceptingOptionSpec<?>> OPTS;

    static {
        OPTS = addOptions();
    }

    private static Map<CalculationOption<?>, ArgumentAcceptingOptionSpec<?>> addOptions() {
        ImmutableMap.Builder<CalculationOption<?>, ArgumentAcceptingOptionSpec<?>> b = ImmutableMap.builder();
        for (CalculationOption<?> option : CalculationOption.values()) {
            @SuppressWarnings("unchecked")
            ArgumentAcceptingOptionSpec<?> spec = PARSER.accepts(option.camelCaseName)
                    .withRequiredArg()
                    // this is needed for some weird reason?
                    .ofType((Class<Object>) option.type)
                    .defaultsTo(option.defaultValue);
            b.put(option, spec);
        }
        return b.build();
    }

    private static final CalculatorConverter CALC_CONV = new CalculatorConverter();

    private static final ArgumentAcceptingOptionSpec<ValueWrapper<FF2Calculator>> INDEX_CALCULATOR =
            PARSER.acceptsAll(Arrays.asList("c", "calculator"), "Calculator for FF2 index")
                    .withRequiredArg()
                    .withValuesConvertedBy(CALC_CONV)
                    .defaultsTo(CALC_CONV.convert("categoryGrouping"));

    private static final ArgumentAcceptingOptionSpec<String> DOMAIN =
            PARSER.acceptsAll(Arrays.asList("d", "domain"), "Domain for the resource pack")
                    .withRequiredArg()
                    .required();

    private static final ArgumentAcceptingOptionSpec<Path> OUTPUT_DIR =
            PARSER.acceptsAll(Arrays.asList("o", "output"), "Output folder for the pack")
                    .withRequiredArg()
                    .withValuesConvertedBy(new PathConverter())
                    .required();

    private static final ArgumentAcceptingOptionSpec<Path> RESOURCES_DIR =
            PARSER.acceptsAll(Arrays.asList("r", "resources"), "Resources to process")
                    .withRequiredArg()
                    .withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING))
                    .defaultsTo(Paths.get("."));

    @Override
    public String getId() {
        return "console";
    }

    @Override
    public void run(String[] args) throws IOException {
        Optional<OptionSet> optsional = JOptCommon.parse(PARSER, args);

        if (!optsional.isPresent()) {
            return;
        }

        OptionSet opts = optsional.get();

        Path outputDir = opts.valueOf(OUTPUT_DIR);
        if (Files.exists(outputDir) && !Files.isDirectory(outputDir)) {
            System.err.println("Error: output directory must be a directory.");
            throw Exit.with(1);
        }
        Files.createDirectories(outputDir);
        Path resourcesDir = opts.valueOf(RESOURCES_DIR);
        String domain = opts.valueOf(DOMAIN);
        FF2Calculator calc = opts.valueOf(INDEX_CALCULATOR).getValue();

        GenericMap calcOptMap = GenericMaps.mutableMap(
                OPTS.entrySet().stream()
                        .map(e -> Maps.immutableEntry(e.getKey(), opts.valueOf(e.getValue())))
                        .collect(toImmutableMap(Entry::getKey, Entry::getValue)));
        CalculationOptions options = CalculationOptions.create(calcOptMap);

        Map<RId, Path> res = gatherResources(domain, resourcesDir);
        FF2Index index = calc.calculate(res, options);
        new FF2ResourcePackWriter(outputDir).write(index, k -> Files.newInputStream(res.get(k)));
    }

    private Map<RId, Path> gatherResources(String domain, Path resourcesDir) throws IOException {
        String basePrefix = resourcesDir.toAbsolutePath().toString();
        if (!basePrefix.endsWith("/")) {
            basePrefix += "/";
        }
        try (Stream<Path> paths = Files.walk(resourcesDir)) {
            String finalizedPrefix = basePrefix;
            return paths
                    .filter(p -> Files.isRegularFile(p))
                    .collect(toImmutableMap(p -> {
                        // Extract base dir
                        String baseDir = p.getParent().toAbsolutePath().toString().replace(finalizedPrefix, "");
                        return RId.from(domain, baseDir, p.getFileName().toString());
                    }, Function.identity()));
        }
    }

}
