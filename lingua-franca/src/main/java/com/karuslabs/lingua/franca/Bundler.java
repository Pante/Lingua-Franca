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
import com.google.common.collect.Lists;

import com.karuslabs.lingua.franca.spi.BundleProvider;

import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.concurrent.TimeUnit.MINUTES;
import com.karuslabs.lingua.franca.annotations.Namespace;


public class Bundler {    
    
    private static final Bundler BUNDLER = new Bundler(CacheBuilder.newBuilder().expireAfterAccess(10, MINUTES).maximumSize(512).build(), BundleLoader.loader());
    private static final ResourceBundle.Control CONTROL = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT);
    
    public static Bundler bundler() {
        return BUNDLER;
    }
    
    
    static final ThreadLocal<ServiceLoader<BundleProvider>> PROVIDERS = new ThreadLocal<>() {
        @Override
        protected ServiceLoader<BundleProvider> initialValue() {
            return ServiceLoader.load(BundleProvider.class);
        }
    };
    
    
    private final Cache<String, Bundle> cache;
    private final BundleLoader loader;
    
    
    protected Bundler(Cache<String, Bundle> cache, BundleLoader loader) {
        this.cache = cache;
        this.loader = loader;
    }
    
    
    public Bundle reload(Object annotated, Locale locale) {
        return reload(annotated, locale, loader);
    }
    
    public Bundle reload(Object annotated, Locale locale, BundleLoader loader) {
        return reload(annotated.getClass(), locale, loader);
    }
    
    
    public Bundle reload(Class<?> annotated, Locale locale) {
        return reload(annotated, locale, loader);
    }
    
    public Bundle reload(Class<?> annotated, Locale locale, BundleLoader loader) {
        return load(annotated, locale, loader, true);
    }
    
    
    public Bundle reload(String name, Locale locale) {
        return reload(name, locale, loader);
    }
    
    public Bundle reload(String name, Locale locale, BundleLoader loader) {
        return load(name, locale, loader, true);
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
        return load(annotated, locale, loader, false);
    }
    
    
    protected Bundle load(Class<?> annotated, Locale locale, BundleLoader loader, boolean reload) {
        var namespace = annotated.getAnnotation(Namespace.class);
        if (namespace != null) {
            return load(namespace.value(), locale, loader, reload);
            
        } else {
            return Bundle.EMPTY;
        }
    }
    
    
    public Bundle load(String name, Locale locale) {
        return load(name, locale, loader);
    }
    
    public Bundle load(String name, Locale locale, BundleLoader loader) {
       return load(name, locale, loader, false);
    }
    
    
    protected Bundle load(String name, Locale locale, BundleLoader loader, boolean reload) {
        var bundleName = CONTROL.toBundleName(name, locale);
        Bundle bundle = null;
        
        if (!reload) {
            bundle = cache.getIfPresent(bundleName);
        }
        
        if (bundle == null) {
            bundle = loadFromServices(name, locale);
        }
        
        if (bundle == null) {
            bundle = loadFromBundleLoader(name, Lists.reverse(loader.parents(name, locale)), loader, reload);
        }
        
        return bundle;
    }
    
    
    protected @Nullable Bundle loadFromServices(String name, Locale locale) {
        try {
            var providers = PROVIDERS.get().iterator();
            while (providers.hasNext()) {
                var bundle = providers.next().get(name, locale);
                if (bundle != null) {
                    cache(name, locale, bundle);
                    return bundle;
                }
            }
            
        } catch (ServiceConfigurationError ignored) {
            // Ignore error
        }
        
        return null;
    }
    
    protected void cache(String name, Locale locale, Bundle bundle) {
        do {
            cache.put(CONTROL.toBundleName(name, locale), bundle);
            bundle = bundle.parent();
            locale = bundle.locale();
        } while (bundle != Bundle.EMPTY);
    }
    
    
    protected Bundle loadFromBundleLoader(String name, List<Locale> locales, BundleLoader loader, boolean reload) {
        var current = Bundle.EMPTY;
        for (var locale : locales) {
            var bundleName = CONTROL.toBundleName(name, locale);
            
            Bundle child = null;
            if (!reload) {
                child = cache.getIfPresent(bundleName);
            }
            
            if (child == null) {
                child = loader.load(name, locale, current);
            }
            
            if (child != Bundle.EMPTY) {
                current = child;
            }
            
            cache.put(bundleName, current);
        }
        
        return current;
    }
    
    
    public BundleLoader loader() {
        return loader;
    }
    
    protected Cache<String, Bundle> cache() {
        return cache;
    }
    
}
