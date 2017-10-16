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
package com.techshroom.unplanned.rp;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.ImmutableMap;

/**
 * A language resource allows access to the language keys stored in the data,
 * and will do resolution to the default language file if needed.
 */
public class LangResource implements VirtualResource {

    private static final Locale DEFAULT = Locale.US;

    /**
     * A translator is a function from a language key to the literal text. It is
     * already bound to a specific {@link Locale}.
     */
    public interface Translator {

        String tr(String key);

        default String tr(String key, Object... args) {
            return String.format(tr(key), args);
        }

    }

    // Current strategy: load default locale
    // Load another if needed

    private static RId getLocalizedId(Locale locale, RId resourceId) {
        // take ${DOMAIN}:lang/name and append '_en_US' like
        RId actualRId = RId.parse(resourceId + "_" + locale.getLanguage() + "_" + locale.getCountry() + ".lang");
        return actualRId;
    }

    private static Map<String, String> loadLangKeys(RId resourceId, ResourcePack pack) {
        RawResource res = pack.loadResource(resourceId);
        return res.useReader(reader -> {
            ImmutableMap.Builder<String, String> lk = ImmutableMap.builder();
            Properties props = new Properties();
            props.load(reader);
            for (String s : props.stringPropertyNames()) {
                lk.put(s, props.getProperty(s));
            }
            return lk.build();
        });
    }

    private final RId resourceId;
    private final ResourcePack source;
    private final Map<String, String> defaultLangKeys;
    private Map<String, String> langKeys;
    private Locale currentLocale;

    public LangResource(RId resourceId, ResourcePack source) {
        this.resourceId = resourceId;
        this.source = source;
        this.defaultLangKeys = loadLangKeys(getLocalizedId(DEFAULT, resourceId), source);
        // load default locale
        translate(Locale.getDefault(), "");
    }

    /**
     * A simple translation function, using a locale and language key.
     * 
     * @param locale
     *            - the locale to translate in
     * @param key
     *            - the language key to look up
     * @return the translated
     */
    public String translate(Locale locale, String key) {
        // fast-path if locale = DEFAULT
        if (locale.equals(DEFAULT)) {
            return defaultLangKeys.getOrDefault(key, key);
        }

        // init map if needed
        if (locale != currentLocale) {
            langKeys = loadLangKeys(getLocalizedId(locale, resourceId), source);
            currentLocale = locale;
        }

        String a = langKeys.get(key);
        if (a == null) {
            return translate(DEFAULT, key);
        }

        return a;
    }

    public Translator translator(Locale locale) {
        return k -> translate(locale, k);
    }

}
