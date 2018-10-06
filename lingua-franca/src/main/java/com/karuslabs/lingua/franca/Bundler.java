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

import com.karuslabs.lingua.franca.annotations.Bundled;
import com.karuslabs.lingua.franca.spi.BundleProvider;

import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.concurrent.TimeUnit.MINUTES;


/**
 * {@code Bundler}s contains a cache and facilities from which a {@code Bundle} is obtained.
 * 
 * Time-based and size cache eviction is enabled for the global {@code Bundler}. 
 * Entries expire either 10 minutes after access or the cache exceeds 512 entries. 
 * By default, the global {@code Bundler} uses the global {@code BundleLoader}.
 * 
 * In addition to retrieving bundles using a {@code BundeLoader}, the default implementation 
 * supports retrieval of {@code Bundle}s from a {@link BundleProvider} service provided 
 * via the JDK's SPI mechanism.
 * 
 * The default implementation first retrieves a bundle from the cache. If unavailable 
 * or cache look-up is disabled, the implementation then retrieves the bundle from a 
 * {@code BundleProvider} and subsequently the given {@code BundleLoader}. This process 
 * is continued recursively until the bundle and its parents are retrieved and linked.
 * In the event a bundle could not be retrieved, an empty bundle is created and linked.
 * 
 * The default implementation is thread-safe and non-blocking.
 */
public class Bundler {    
    
    private static final Bundler BUNDLER = new Bundler(CacheBuilder.newBuilder().expireAfterAccess(10, MINUTES).maximumSize(512).build(), BundleLoader.loader());
    private static final ResourceBundle.Control CONTROL = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT);
    
    /**
     * Returns the global {@code Bundler} which uses the global {@code BundleLoader} by default.
     * 
     * @return the global bundler
     */
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
    
    
    /**
     * Creates a {@code Bundler} with the specified cache and default {@code BundleLoader}.
     * 
     * @param cache the cache
     * @param loader the BundleLoader
     */
    protected Bundler(Cache<String, Bundle> cache, BundleLoader loader) {
        this.cache = cache;
        this.loader = loader;
    }
    
    
    /**
     * Reloads the bundle and its parents using the default {@code BundleLoader},
     * specified locale and @{link Bundled} annotation, invalidating prior cached entries. 
     * An empty bundle is returned if no annotation is available.
     * 
     * @param annotated the annotated object
     * @param locale the locale
     * @return the retrieved bundle, or an empty bundle if no annotation is available
     */
    public Bundle reload(Object annotated, Locale locale) {
        return reload(annotated, locale, loader);
    }
    
    /**
     * Reloads the bundle and its parents using the specified {@code BundleLoader},
     * locale and @{link Bundled} annotation, invalidating prior cached entries. 
     * An empty bundle is returned if no annotation is available.
     * 
     * @param annotated the annotated object
     * @param locale the locale
     * @param loader the BundleLoader
     * @return the retrieved bundle, or an empty bundle if no annotation is available
     */
    public Bundle reload(Object annotated, Locale locale, BundleLoader loader) {
        return reload(annotated.getClass(), locale, loader);
    }
    
    
    /**
     * Reloads the bundle and its parents using the default {@code BundleLoader},
     * specified locale and @{link Bundled} annotation, invalidating prior cached entries. 
     * An empty bundle is returned if no annotation is available.
     * 
     * @param annotated the annotated class
     * @param locale the locale
     * @return the retrieved bundle, or an empty bundle if no annotation is available
     */
    public Bundle reload(Class<?> annotated, Locale locale) {
        return reload(annotated, locale, loader);
    }
    
    /**
     * Reloads the bundle and its parents using the specified {@code BundleLoader},
     * locale and @{link Bundled} annotation, invalidating prior cached entries. 
     * An empty bundle is returned if no annotation is available.
     * 
     * @param annotated the annotated class
     * @param locale the locale
     * @param loader the BundleLoader
     * @return the retrieved bundle, or an empty bundle if no annotation is available
     */
    public Bundle reload(Class<?> annotated, Locale locale, BundleLoader loader) {
        return load(annotated, locale, loader, true);
    }
    
    
    /**
     * Reloads the bundle and its parents using the default {@code BundleLoader}, 
     * and specified name and locales, invalidating prior cached entries.
     * 
     * @param name the base name
     * @param locale the locale
     * @return the retrieved bundle
     */
    public Bundle reload(String name, Locale locale) {
        return reload(name, locale, loader);
    }
    
    /**
     * Reloads the bundle and its parents using the specified {@code BundleLoader}, 
     * and specified name and locales, invalidating prior cached entries.
     * 
     * @param name the base name
     * @param locale the locale
     * @param loader the BundleLoader
     * @return the retrieved bundle
     */
    public Bundle reload(String name, Locale locale, BundleLoader loader) {
        return load(name, locale, loader, true);
    }

    
    /**
     * Loads the bundle and its parents using the default {@code BundleLoader} and 
     * specified locale and @{link Bundled} annotation. 
     * An empty bundle is returned if no annotation is available.
     * 
     * @param annotated the annotated object
     * @param locale the locale
     * @return the retrieved bundle, or an empty bundle if no annotation is available
     */
    public Bundle load(Object annotated, Locale locale) {
        return load(annotated, locale, loader);
    }
    
    /**
     * Loads the bundle and its parents using the specified {@code BundleLoader},
     * locale and @{link Bundled} annotation. 
     * An empty bundle is returned if no annotation is available.
     * 
     * @param annotated the annotated object
     * @param locale the locale
     * @param loader the BundleLoader
     * @return the retrieved bundle, or an empty bundle if no annotation is available
     */
    public Bundle load(Object annotated, Locale locale, BundleLoader loader) {
        return load(annotated.getClass(), locale, loader);
    }
    
    
    /**
     * Loads the bundle and its parents using the default {@code BundleLoader} and 
     * specified locale and @{link Bundled} annotation. 
     * An empty bundle is returned if no annotation is available.
     * 
     * @param annotated the annotated class
     * @param locale the locale
     * @return the retrieved bundle, or an empty bundle if no annotation is available
     */
    public Bundle load(Class<?> annotated, Locale locale) {
        return load(annotated, locale, loader);
    }
    
    /**
     * Loads the bundle and its parents using the specified {@code BundleLoader},
     * locale and @{link Bundled} annotation. 
     * An empty bundle is returned if no annotation is available.
     * 
     * @param annotated the annotated class
     * @param locale the locale
     * @param loader the BundleLoader
     * @return the retrieved bundle, or an empty bundle if no annotation is available
     */
    public Bundle load(Class<?> annotated, Locale locale, BundleLoader loader) {
        return load(annotated, locale, loader, false);
    }
    
    
    /**
     * Loads the bundle and its parents using the specified {@code BundleLoader},
     * locale, @{link Bundled} annotation and reload flag.
     * An empty bundle is returned if no annotation is available.
     * 
     * @param annotated the annotated class
     * @param locale the locale
     * @param loader the BundleLoader
     * @param reload the reload flag; true if cache-lookup is disabled
     * @return the retrieved bundle, or an empty bundle if no annotation is available
     */
    protected Bundle load(Class<?> annotated, Locale locale, BundleLoader loader, boolean reload) {
        var bundled = annotated.getAnnotation(Bundled.class);
        if (bundled != null) {
            return load(bundled.value(), locale, loader, reload);
            
        } else {
            return Bundle.EMPTY;
        }
    }
    
    
    /**
     * Loads the bundle and its parents using the default {@code BundleLoader} and
     * specified locale and base name. 
     * 
     * @param name the base name
     * @param locale the locale
     * @return the retrieved bundle
     */
    public Bundle load(String name, Locale locale) {
        return load(name, locale, loader);
    }
    
    /**
     * Loads the bundle and its parents using the specified {@code BundleLoader},
     * locale and base name. 
     * 
     * @param name the base name
     * @param locale the locale
     * @param loader the BundleLoader
     * @return the retrieved bundle
     */
    public Bundle load(String name, Locale locale, BundleLoader loader) {
       return load(name, locale, loader, false);
    }
    
    
    /**
     * Loads the bundle and its parents using the specified {@code BundleLoader},
     * locale, base name and reload flag. 
     * 
     * @param name the base name
     * @param locale the locale
     * @param loader the BundleLoader
     * @param reload the reload flag; true if cache-lookup is disabled
     * @return the retrieved bundle
     */
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
    
    
    /**
     * Retrieves a {@code Bundle} and its parents from a {@code BundleProvider} service,
     * or null if unavailable.
     * 
     * @param name the base name of the bundle to be retrieved
     * @param locale the locale of the bundle to be retrieved
     * @return the retrieved bundle, or null if unavailable
     */
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
    
    /**
     * Caches the specified bundle and its parents using the specified base name
     * and initial locale.
     * 
     * @param name the base name
     * @param locale the initial locale
     * @param bundle the bundle to be cached
     */
    protected void cache(String name, Locale locale, Bundle bundle) {
        do {
            cache.put(CONTROL.toBundleName(name, locale), bundle);
            bundle = bundle.parent();
            locale = bundle.locale();
        } while (bundle != Bundle.EMPTY);
    }
    
    
    /**
     * Loads, links and caches the bundles associated with the specified base name
     * and locales using the specified {@code BundleLoader} and reload flag.
     * 
     * @param name the base name
     * @param locales the locales
     * @param loader the BundleLoader
     * @param reload the reload flag; true if cache-lookup is disabled
     * @return the leaf bundle
     */
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
    
    
    /**
     * Returns the default {@code BundleLoader}.
     * 
     * @return the default BundleLoader
     */
    public BundleLoader loader() {
        return loader;
    }
    
    /**
     * Returns the cache for {@code Bundle}s.
     * 
     * @return the cache
     */
    protected Cache<String, Bundle> cache() {
        return cache;
    }
    
}
