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


/**
 * A {@code Bundler} contains facilities from which a bundle is cached and obtained.
 * <p>
 * Time-based and size eviction is enabled for the global {@code Bundler} cache. 
 * Entries expire either 10 minutes after access or if the cache exceeds 512 entries.
 * <p>
 * By default, the global {@code Bundler} uses the global {@link BundleLoader#loader() BundleLoader}.
 * In addition to loading bundles using a {@code BundeLoader}, the default implementation 
 * supports loading of {@code Bundle}s using a {@link BundleProvider} service provided 
 * via the SPI mechanism.
 * <p>
 * The default implementation first attempts to retrieve a bundle from the cache. 
 * If cache look-up is disabled or the bundle is not cached, the implementation then 
 * attempts to retrieve the bundle from a {@code BundleProvider} and subsequently a 
 * given {@code BundleLoader}. This process is repeated recursively until the bundle and its parents 
 * are loaded and linked. In the event a bundle could not be retrieved, an empty bundle 
 * is created and linked. This process is thread-safe and non-blocking.
 */
public class Bundler {    
    
    private static final Bundler BUNDLER = new Bundler(CacheBuilder.newBuilder().expireAfterAccess(10, MINUTES).maximumSize(512).build(), BundleLoader.loader());
    private static final ResourceBundle.Control CONTROL = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT);
    
    /**
     * Returns the global {@code Bundler} which uses the global {@code BundleLoader}.
     * 
     * @return the global Bundler
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
     * Creates a {@code Bundler} with the specified cache and {@code BundleLoader}.
     * 
     * @param cache the cache
     * @param loader the default BundleLoader
     */
    protected Bundler(Cache<String, Bundle> cache, BundleLoader loader) {
        this.cache = cache;
        this.loader = loader;
    }
    
    
    /**
     * Reloads the bundle and its parents using the default {@code BundleLoader},
     * {@link Bundled} annotation on the specified annotated object and locale, 
     * invalidating cached entries. An empty bundle is returned if no annotation was found.
     * 
     * @param annotated the annotated object
     * @param locale the locale
     * @return the bundle, or an empty bundle if no annotation was found
     */
    public Bundle reload(Object annotated, Locale locale) {
        return reload(annotated, locale, loader);
    }
    
    /**
     * Reloads the bundle and its parents using the specified {@code BundleLoader},
     * {@link Bundled} annotation on the specified annotated object and locale,
     * invalidating cached entries. An empty bundle is returned if no annotation was found.
     * 
     * @param annotated the annotated object
     * @param locale the locale
     * @param loader the BundleLoader
     * @return the bundle, or an empty bundle if no annotation was found
     */
    public Bundle reload(Object annotated, Locale locale, BundleLoader loader) {
        return reload(annotated.getClass(), locale, loader);
    }
    
    
    /**
     * Reloads the bundle and its parents using the default {@code BundleLoader},
     * {@link Bundled} annotation on the specified annotated class and locale,
     * invalidating cached entries. An empty bundle is returned if no annotation was found.
     * 
     * @param annotated the annotated class
     * @param locale the locale
     * @return the bundle, or an empty bundle if no annotation was found
     */
    public Bundle reload(Class<?> annotated, Locale locale) {
        return reload(annotated, locale, loader);
    }
    
    /**
     * Reloads the bundle and its parents using the specified {@code BundleLoader},
     * {@link Bundled} annotation on the specified annotated class and locale,
     * invalidating cached entries. An empty bundle is returned if no annotation was found.
     * 
     * @param annotated the annotated class
     * @param locale the locale
     * @param loader the BundleLoader
     * @return the bundle, or an empty bundle if no annotation was found
     */
    public Bundle reload(Class<?> annotated, Locale locale, BundleLoader loader) {
        return load(annotated, locale, loader, true);
    }
    
    
    /**
     * Reloads the bundle and its parents using the default {@code BundleLoader}
     * and specified name and locales, invalidating cached entries.
     * 
     * @param name the base name
     * @param locale the locale
     * @return the bundle
     */
    public Bundle reload(String name, Locale locale) {
        return reload(name, locale, loader);
    }
    
    /**
     * Reloads the bundle and its parents using the specified {@code BundleLoader}
     * and specified name and locales, invalidating cached entries.
     * 
     * @param name the base name
     * @param locale the locale
     * @param loader the BundleLoader
     * @return the bundle
     */
    public Bundle reload(String name, Locale locale, BundleLoader loader) {
        return load(name, locale, loader, true);
    }

    
    /**
     * Loads the bundle and its parents using the default {@code BundleLoader}, 
     * {@link Bundled} annotation on the specified annotated object and locale. 
     * An empty bundle is returned if no annotation was found.
     * 
     * @param annotated the annotated object
     * @param locale the locale
     * @return the bundle, or an empty bundle if no annotation was found
     */
    public Bundle load(Object annotated, Locale locale) {
        return load(annotated, locale, loader);
    }
    
    /**
     * Loads the bundle and its parents using the specified {@code BundleLoader},
     * {@link Bundled} annotation on the specified annotated object and locale. 
     * An empty bundle is returned if no annotation was found.
     * 
     * @param annotated the annotated object
     * @param locale the locale
     * @param loader the BundleLoader
     * @return the bundle, or an empty bundle if no annotation was found
     */
    public Bundle load(Object annotated, Locale locale, BundleLoader loader) {
        return load(annotated.getClass(), locale, loader);
    }
    
    
    /**
     * Loads the bundle and its parents using the default {@code BundleLoader}, 
     * {@link Bundled} annotation on the specified annotated class and specified locale. 
     * An empty bundle is returned if no annotation was found.
     * 
     * @param annotated the annotated class
     * @param locale the locale
     * @return the bundle, or an empty bundle if no annotation was found
     */
    public Bundle load(Class<?> annotated, Locale locale) {
        return load(annotated, locale, loader);
    }
    
    /**
     * Loads the bundle and its parents using the specified {@code BundleLoader},
     * {@link Bundled} annotation on the specified annotated class and locale. 
     * An empty bundle is returned if no annotation was found.
     * 
     * @param annotated the annotated class
     * @param locale the locale
     * @param loader the BundleLoader
     * @return the bundle, or an empty bundle if no annotation was found
     */
    public Bundle load(Class<?> annotated, Locale locale, BundleLoader loader) {
        return load(annotated, locale, loader, false);
    }
    
    
    /**
     * Loads the bundle and its parents using the specified {@code BundleLoader},
     * {@link Bundled} annotation on the specified annotated class, locale and reload flag.
     * An empty bundle is returned if no annotation was found.
     * 
     * @param annotated the annotated class
     * @param locale the locale
     * @param loader the BundleLoader
     * @param reload the reload flag; true if cache-lookup is to be disabled
     * @return the bundle, or an empty bundle if no annotation was found
     */
    protected Bundle load(Class<?> annotated, Locale locale, BundleLoader loader, boolean reload) {
        var bundled = annotated.getAnnotation(Namespace.class);
        if (bundled != null) {
            return load(bundled.value(), locale, loader, reload);
            
        } else {
            return Bundle.EMPTY;
        }
    }
    
    
    /**
     * Loads the bundle and its parents using the default {@code BundleLoader},
     * specified locale and base name. 
     * 
     * @param name the base name
     * @param locale the locale
     * @return the bundle
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
     * @return the bundle
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
     * @param reload the reload flag; true if cache-lookup is to be disabled
     * @return the bundle
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
     * Loads the bundle and its parents using a {@code BundleProvider} service.
     * 
     * @param name the base name
     * @param locale the locale
     * @return the bundle, or null if no BundleProvider for the bundle name was registered
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
     * Caches the specified bundle with the base name and initial locale,
     * and its parents.
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
     * Loads and caches the bundles associated with the specified base name
     * and locales using the {@code BundleLoader}.
     * 
     * @param name the base name
     * @param locales the locales
     * @param loader the BundleLoader
     * @param reload the reload flag; true if cache-lookup is to be disabled
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
     * Returns the {@code BundleLoader}.
     * 
     * @return the BundleLoader
     */
    public BundleLoader loader() {
        return loader;
    }
    
    /**
     * Returns the cached bundles.
     * 
     * @return the cache
     */
    protected Cache<String, Bundle> cache() {
        return cache;
    }
    
}
