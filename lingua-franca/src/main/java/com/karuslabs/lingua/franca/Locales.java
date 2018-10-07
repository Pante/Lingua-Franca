/*
 * The MIT License
 *
 * Copyright 2018 Karus Labs.
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
package com.karuslabs.lingua.franca;

import com.google.common.cache.*;
import com.google.common.collect.*;

import java.util.*;

import static java.util.Locale.ENGLISH;
import static java.util.concurrent.TimeUnit.MINUTES;


/**
 * This class consists exclusively of static methods which operate on or return {@code Locale}s.
 * <p>
 * Language tags used in the static methods of this class which accept language tags
 * can be separated by either '_' or '-', i.e. 'en_GB' or 'en-GB'. 
 */
public class Locales {
    
    private static final BiMap<String, Locale> LOCALES;
    
    private static final Cache<String, Locale> TAG_CACHE = CacheBuilder.newBuilder().maximumSize(512).expireAfterAccess(5, MINUTES).build();
    private static final Cache<Locale, String> LOCALE_CACHE = CacheBuilder.newBuilder().maximumSize(512).expireAfterAccess(5, MINUTES).build();
    
    private static final Set<String> LANGUAGES = Set.of(Locale.getISOLanguages());
    private static final Set<String> COUNTRIES = Set.of(Locale.getISOCountries());
    
    
    static {
        var locales = Locale.getAvailableLocales();
        LOCALES = HashBiMap.create(locales.length);
        
        for (var locale : locales) {
            LOCALES.put(locale.toLanguageTag(), locale);
        }
    }

    
    /**
     * Creates a {@code Locale} which best represents the specified language tag.
     * 
     * @param tag the language tag
     * @return a locale which best represents the specified language tag
     */
    public static Locale of(String tag) {
        tag = tag.replace('_', '-');
        var locale = LOCALES.get(tag);
        
        if (locale == null) {
            locale = TAG_CACHE.getIfPresent(tag);
            
            if (locale == null) {
                locale = Locale.forLanguageTag(tag);
                TAG_CACHE.put(tag, locale);
            }
        }
        
        return locale;
    }
    
    
    /**
     * Creates a language tag delimited by '-' for the specified locale.
     * 
     * @param locale the locale
     * @return the language tag
     */
    public static String of(Locale locale) {
        return of(locale, '-');
    }
    
    /**
     * Creates a language tag delimited by the specified delimiter for the specified locale.
     * 
     * @param locale the locale
     * @param delimiter the delimiter
     * @return the language tag
     */
    public static String of(Locale locale, char delimiter) {
        var tag = LOCALES.inverse().get(locale);
        
        if (tag == null) {
            tag = LOCALE_CACHE.getIfPresent(locale);
            
            if (tag == null) {
                tag = locale.toLanguageTag(); 
                LOCALE_CACHE.put(locale, tag);
            }
        }
        
        return tag.replace('-', delimiter);
    }
    
    
    /**
     * Returns true if the specified language is an ISO 3166 alpha-2 language i.e. {@code EN}.
     * 
     * @param language the language
     * @return true if the specified country is an ISO 3166 alpha-2 language
     */
    public static boolean isISOLanguage(String language) {
        return language.length() == 2 && LANGUAGES.contains(language.toLowerCase(ENGLISH));
    }
    
    /**
     * Returns true if the specified country is an ISO 639 alpha-2 country i.e. {@code UK}.
     * 
     * @param country the country
     * @return true if the specified country is an ISO 639 alpha-2 country country
     */
    public static boolean isISOCountry(String country) {
        return country.length() == 2 && COUNTRIES.contains(country.toUpperCase(ENGLISH));
    }
    
}
