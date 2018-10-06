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

import com.karuslabs.lingua.franca.annotations.*;
import com.karuslabs.lingua.franca.codec.Stringifier;
import com.karuslabs.lingua.franca.sources.*;

import java.io.IOException;
import java.util.*;
import java.util.ResourceBundle.Control;
import java.util.concurrent.*;

import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * {@code BundleLoader}s contains a global and namespace registry from which a {@code Bundle} is 
 * retrieved and loaded. Each member in a family of {@code Bundle}s share a base name and 
 * hence, a common namespace. 
 * 
 * The default implementation first retrieves a bundle from the namespace registry
 * and subsequently the global registry. In the event a bundle could not be retrieved, an
 * empty bundle is returned. The entire retrieval and loading is thread-safe.
 * 
 * The default implementation has support for properties, JSON and YAML file formats.
 */
public class BundleLoader {
    
    private static final BundleLoader LOADER = new BundleLoader();
    
    
    /**
     * Returns the global {@code BundleLoader}.
     * 
     * @return the global BundleLoader
     */
    public static BundleLoader loader() {
        return LOADER;
    }
    
    
    private static final Control CONTROL = ResourceBundle.Control.getControl(Control.FORMAT_DEFAULT);
    private static final Source[] SOURCE = new Source[] {};

    protected final String[] formats;
    protected final ConcurrentMap<String, Set<Source>> namespaces;
    protected final Set<Source> global;

    
    /**
     * Creates a {@BundleLoader} with empty registries and default supported formats.
     */
    public BundleLoader() {
        this(new ConcurrentHashMap<>(), ConcurrentHashMap.newKeySet(), "json", "properties", "yml", "yaml");
    }
    
    /**
     * Creates a {@code BundleLoader} with the specified registries and supported formats.
     * 
     * @param namespaces the namespace registry
     * @param global the global registry
     * @param formats the supported formats
     */
    public BundleLoader(ConcurrentMap<String, Set<Source>> namespaces, Set<Source> global, String... formats) {
        this.namespaces = namespaces;
        this.global = global;
        this.formats = formats;
    }

    
    /**
     * Retrieves a {@code Bundle} with the specified name, locale and parent bundle.
     * 
     * @param name the bundle name
     * @param locale the bundle locale
     * @param parent the parent of the bundle to be retrieved
     * @return the retrieved bundle, or an empty bundle if unavailable
     */
    public Bundle load(String name, Locale locale, Bundle parent) {
        var sources = namespaces.getOrDefault(name, global);
        var bundleName = CONTROL.toBundleName(name, locale);
        
        Map<String, Object> messages = null;
        
        if (!sources.isEmpty()) {
            messages = load(sources, bundleName);
        }
         
        if (messages == null && sources != global) {
            messages = load(global, bundleName);
        }
        
        return messages == null ? Bundle.empty(locale, parent) : new Bundle(messages, locale, parent);
    }
    
    /**
     * Retrieves the contents of a bundle with the specified transformed bundle name, 
     * i.e. "name_en_GB" from the specified sources.
     * 
     * @param sources the sources from which the bundle is retrieved
     * @param bundle the transformed bundle name, i.e. "name_en_GB"
     * @return the retrieved contents of a bundle
     */
    protected @Nullable Map<String, Object> load(Set<Source> sources, String bundle) {
        for (var source : sources) {
            for (var format : formats) {
                try (var stream = source.load(CONTROL.toResourceName(bundle, format))) {
                    if (stream != null) {
                        return Stringifier.stringify().from(stream, format);
                    }
                    
                } catch (IOException ignored) {
                    return null;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Returns a list of locales for the specified base name and locale which includes itself.
     * The default implementation forwards to {@link ResourceBundle.Control#getCandidateLocales(String, Locale)}.
     * 
     * @param name the base name
     * @param locale the locale
     * @return a list of parent locales which includes itself
     */
    public List<Locale> parents(String name, Locale locale) {
        return CONTROL.getCandidateLocales(name, locale);
    }
    
    /**
     * Registers the sources specified in the {@link ClassLoaderSources}, {@link ModuleSources} 
     * and {@link SystemSources} annotations to the namespace specified in the {@link Bundled}
     * annotation if present, else the global registry.
     * 
     * @param annotated the annotated object
     * @return true if the registry did not already contain the specified sources
     */
    public boolean add(Object annotated) {
        return add(annotated.getClass());
    }
    
    /**
     * Registers the sources specified in the {@link ClassLoaderSources}, {@link ModuleSources} 
     * and {@link SystemSources} annotations to the namespace specified in the {@link Bundled}
     * annotation if present, else the global registry.
     * 
     * @param annotated the annotated class
     * @return true if the registry did not already contain the specified sources
     */
    public boolean add(Class<?> annotated) {
        var bundled = annotated.getAnnotation(Bundled.class);
        var sources = parse(annotated).toArray(SOURCE);
        
        return bundled == null ? add(sources) : add(bundled.value(), sources);
    }
    
    /**
     * Creates a list of sources from the annotations in the specified class.
     * 
     * @param annotated the annotated class
     * @return the sources created from the specified annotated class
     */
    protected List<Source> parse(Class<?> annotated) {
        var sources = new ArrayList<Source>();
        
        var classpaths = annotated.getAnnotation(ClassLoaderSources.class);
        if (classpaths != null) {
            var classloader = annotated.getClassLoader();
            
            for (var path : classpaths.value()) {
                sources.add(new ClassLoaderSource(classloader, path));
            }
        }
        
        var modulepaths = annotated.getAnnotation(ModuleSources.class);
        if (modulepaths != null) {
            var module = annotated.getModule();
            
            for (var path : modulepaths.value()) {
                sources.add(new ModuleSource(module, path));
            }
        }
        
        var paths = annotated.getAnnotation(SystemSources.class);
        if (paths != null) {
            for (var path : paths.value()) {
                sources.add(new SystemSource(path));
            }
        }
        
        return sources;
    }

    
    /**
     * Adds the specified source to the specified namespace.
     * 
     * @param name the namespace
     * @param source the source
     * @return true if the namespace did not already contain the specified source
     */
    public boolean add(String name, Source source) {
        return get(name, 1).add(source);
    }
    
    /**
     * Adds the specified sources to the specified namespace.
     * 
     * @param name the namespace
     * @param sources the sources
     * @return true if the namespace did not already contain any of the specified sources
     */
    public boolean add(String name, Source... sources) {
        return Collections.addAll(get(name, sources.length), sources);
    }
    
    /**
     * Adds the specified sources to the specified namespace.
     * 
     * @param name the namespace
     * @param sources the sources
     * @return true if the namespace did not already contain any of the specified sources
     */
    public boolean add(String name, Collection<? extends Source> sources) {
        return get(name, sources.size()).addAll(sources);
    }
    
    /**
     * Returns a set of sources associated with the specified namespace, creating
     * the set if necessary.
     * 
     * @param name the namespace
     * @param length the initial capacity of the set 
     * @return the sources associated with the specified namespace
     */
    protected Set<Source> get(String name, int length) {
        var set = namespaces.get(name);
        if (set == null) {
            set = ConcurrentHashMap.newKeySet(length);
            namespaces.put(name, set);
        }
        
        return set;
    }
     
    /**
     * Adds the specified source to the global registry.
     * 
     * @param source the source
     * @return true if the global registry did not already contain the source
     */
    public boolean add(Source source) {
        return global.add(source);
    }
    
    /**
     * Adds the specified sources to the global registry.
     * 
     * @param sources the sources
     * @return true if the global registry did not already contain any of the sources
     */
    public boolean add(Source... sources) {
        return Collections.addAll(global, sources);
    }
    
    /**
     * Adds the specified sources to the global registry.
     * 
     * @param sources the sources
     * @return true if the global registry did not already contain any of the sources
     */
    public boolean add(Collection<? extends Source> sources) {
        return global.addAll(sources);
    }
    
    /**
     * Returns true if the namespace registry contains the specified namespace.
     * 
     * @param name the namespace
     * @return true if the namespace contains the specified namespace
     */
    public boolean contains(String name) {
        return namespaces.containsKey(name);
    }
    
    /**
     * Returns true if the namespace registry contains the specified namespace and source.
     * 
     * @param name the namespace
     * @param source the source
     * @return true if the namespace contains the specified namespace and source
     */
    public boolean contains(String name, Source source) {
        var bundle = namespaces.get(name);
        return bundle != null && bundle.contains(source);
    }
    
    /**
     * Returns true if the namespace registry contains the specified namespace and sources.
     * 
     * @param name the namespace
     * @param sources the sources
     * @return true if the namespace contains the specified namespace and sources
     */
    public boolean contains(String name, Source... sources) {
        return contains(name, List.of(sources));
    }
    
    /**
     * Returns true if the namespace registry contains the specified namespace and sources.
     * 
     * @param name the namespace
     * @param sources the sources
     * @return true if the namespace contains the specified namespace and sources
     */
    public boolean contains(String name, Collection<? extends Source> sources) {
        var bundle = namespaces.get(name);
        return bundle != null && bundle.containsAll(sources);
    }
    
    /**
     * Returns true if the global registry contains the specified source.
     * 
     * @param source the source
     * @return true if the global registry contains the specified source
     */
    public boolean contains(Source source) {
        return global.contains(source);
    }
    
    /**
     * Returns true if the global registry contains the specified sources.
     * 
     * @param sources the sources
     * @return true if the global registry contains the specified sources
     */
    public boolean contains(Source... sources) {
        return contains(List.of(sources));
    }
    
    /**
     * Returns true if the global registry contains the specified sources.
     * 
     * @param sources the sources
     * @return true if the global registry contains the specified sources
     */
    public boolean contains(Collection<? extends Source> sources) {
        return global.containsAll(sources);
    }

    /**
     * Removes the specified namespace from the namespace registry.
     * 
     * @param name the namespace
     * @return the sources associated with the specified namespace, or null if the registry did not contain the namespace
     */
    public @Nullable Set<Source> remove(String name) {
        return namespaces.remove(name);
    }
    
    /**
     * Removes the specified source from the specified namespace.
     * 
     * @param name the namespace
     * @param source the source
     * @return true if the namespace registry contained the namespace and the namespace contained the source
     */
    public boolean remove(String name, Source source) {
        var set = namespaces.get(name);
        return set != null && set.remove(source);
    }
    
    /**
     * Removes the specified sources from the specified namespace.
     * 
     * @param name the namespace
     * @param sources the sources
     * @return true if the namespace registry contained the namespace and the namespace contained any of the sources
     */
    public boolean remove(String name, Source... sources) {
        return remove(name, List.of(sources));
    }
    
    /**
     * Removes the specified sources from the specified namespace.
     * 
     * @param name the namespace
     * @param sources the sources
     * @return true if the namespace registry contained the namespace and the namespace contained any of the sources
     */
    public boolean remove(String name, Collection<? extends Source> sources) {
        var set = namespaces.get(name);
        return set != null && set.removeAll(sources);
    }
    
    /**
     * Removes the specified source from the global registry.
     * 
     * @param source the source
     * @return true if the global registry contained the source
     */
    public boolean remove(Source source) {
        return global.remove(source);
    }
    
    /**
     * Removes the specified source from the global registry.
     * 
     * @param sources the sources
     * @return true if the global registry contained any of the sources
     */
    public boolean remove(Source... sources) {
        return remove(List.of(sources));
    }
    
    /**
     * Removes the specified source from the global registry.
     * 
     * @param sources the sources
     * @return true if the global registry contained any of the sources
     */
    public boolean remove(Collection<? extends Source> sources) {
        return global.removeAll(sources);
    }
    
}
