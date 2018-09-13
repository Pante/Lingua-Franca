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
import com.karuslabs.lingua.franca.sources.*;

import java.util.*;
import java.util.ResourceBundle.Control;
import java.util.concurrent.*;

import org.checkerframework.checker.nullness.qual.Nullable;


public class BundleLoader {
    
    private static final Control CONTROL = ResourceBundle.Control.getControl(Control.FORMAT_DEFAULT);
    private static final Source[] SOURCE = new Source[] {};

    protected ConcurrentMap<String, Source[]> namespaces;
    protected Set<Source> global;
    
    
    public BundleLoader() {
        this(new ConcurrentHashMap<>(), ConcurrentHashMap.newKeySet());
    }
    
    public BundleLoader(ConcurrentMap<String, Source[]> namespaces, Set<Source> global) {
        this.namespaces = namespaces;
        this.global = global;
    }

    
    public Bundle load(String base, Locale locale, Bundle parent) {
        
        
        return Bundle.EMPTY;
    }
    
    
    public List<Locale> parents(String base, Locale locale) {
        var locales = CONTROL.getCandidateLocales(base, locale);
        Collections.reverse(locales);
        return locales;
    }
    
    
    public String toResourceName(String bundle, String format) {
        return CONTROL.toResourceName(bundle, format);
    }
    
    public String toBundleName(String base, Locale locale) {
        return CONTROL.toBundleName(base, locale);
    }
    
    
    public boolean register(Object annotated) {
        return register(annotated.getClass());
    }
    
    public boolean register(Class<?> annotated) {
        var bundled = annotated.getAnnotation(Bundled.class);
        var sources = parse(annotated).toArray(SOURCE);
        
        if (bundled != null) {
            return register(bundled.value(), sources) != null;
            
        } else {
            return register(sources);
        }
    }
    
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
            for (var path : paths.values()) {
                sources.add(new SystemSource(path));
            }
        }
        
        return sources;
    }
    
    
    public @Nullable Source[] register(String base, Source... sources) {
        return namespaces.put(base, sources);
    }
    
    public boolean register(Source... sources) {
        return global.addAll(Set.of(sources));
    }
    
    public boolean register(Source source) {
        return global.add(source);
    }

    
    public @Nullable Source[] unregister(String base) {
        return namespaces.remove(base);
    }
    
    public boolean unregister(Source... sources) {
        return global.removeAll(Set.of(sources));
    }
    
    public boolean unregister(Source source) {
        return global.remove(source);
    }
    
        
    public boolean registered(String base) {
        return namespaces.containsKey(base);
    }
    
    public boolean registered(Source... sources) {
        return global.containsAll(Set.of(sources));
    }
    
    public boolean registered(Source source) {
        return global.contains(source);
    }
    
}
