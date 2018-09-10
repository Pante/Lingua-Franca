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

import com.google.common.cache.Cache;

import com.karuslabs.lingua.franca.annotations.Bundled;

import java.util.Locale;


public class Bundler {    
    
    private static Bundler BUNDLER = null;
    
    public static Bundler bundler() {
        return BUNDLER;
    }
    
    
    private Cache<String, Bundle> cache;
    private BundleLoader loader;
    
    
    protected Bundler(Cache<String, Bundle> cache, BundleLoader loader) {
        this.cache = cache;
        this.loader = loader;
    }

    
    public Bundle load(Object annotated, Locale locale) {
        return load(annotated, locale, loader);
    }
    
    public Bundle load(Object annotated, Locale locale, BundleLoader loader) {
        return load(annotated.getClass(), locale, loader);
    }
    
    
    public Bundle load(Class<?> annotated, Locale locale) {
        return load(annotated, locale, loader);
    }
    
    public Bundle load(Class<?> annotated, Locale locale, BundleLoader loader) {
        var bundled = annotated.getAnnotation(Bundled.class);
        if (bundled != null) {
            return load(bundled.value(), locale, loader);
            
        } else {
            return Bundle.EMPTY;
        }
    }
    
    
    public Bundle load(String name, Locale locale) {
        return load(name, locale, loader);
    }
    
    public Bundle load(String name, Locale locale, BundleLoader loader) {
        
    }
    
}
