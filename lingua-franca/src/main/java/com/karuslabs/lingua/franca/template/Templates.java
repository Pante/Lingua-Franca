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
package com.karuslabs.lingua.franca.template;

import com.karuslabs.lingua.franca.*;
import com.karuslabs.lingua.franca.template.annotations.*;

import java.io.*;
import java.lang.StackWalker.Option;
import java.nio.file.*;
import java.util.*;


public class Templates {
    
    private static final BundleLoader LOADER = BundleLoader.loader();
    private static final StackWalker STACK = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
    
    
    public static boolean fromEmbedded(Object annotated) {
        return fromEmbedded(annotated.getClass(), STACK.getCallerClass(), LOADER);
    }

     public static boolean fromEmbedded(Object annotated, BundleLoader loader) {
        return fromEmbedded(annotated.getClass(), STACK.getCallerClass(), loader);
    }
    
     
    public static boolean fromEmbedded(Class<?> annotated) {
        return fromEmbedded(annotated, STACK.getCallerClass(), LOADER);
    }
    
    public static boolean fromEmbedded(Class<?> annotated, BundleLoader loader) {
        return fromEmbedded(annotated, STACK.getCallerClass(), loader);
    }
    
    
    private static boolean fromEmbedded(Class<?> annotated, Class<?> caller, BundleLoader loader) {
        var success = true;
        
        for (var embedded : annotated.getAnnotationsByType(Embedded.class)) {
            var locales = new ArrayList<Locale>(embedded.locales().length);
            for (var locale : embedded.locales()) {
                locales.add(Locales.of(locale));
            }
            
            success &= fromClassLoader(embedded.template(), caller.getClassLoader(), locales, embedded.destination(), loader);
        }
        
        return success;
    }
    
    
    public static boolean fromPlatforms(Object annotated) {
        return fromPlatforms(annotated.getClass(), STACK.getCallerClass(), LOADER);
    }

     public static boolean fromPlatforms(Object annotated, BundleLoader loader) {
        return fromPlatforms(annotated.getClass(), STACK.getCallerClass(), loader);
    }
    
     
    public static boolean fromPlatforms(Class<?> annotated) {
        return fromPlatforms(annotated, STACK.getCallerClass(), LOADER);
    }
    
    public static boolean fromPlatforms(Class<?> annotated, BundleLoader loader) {
        return fromPlatforms(annotated, STACK.getCallerClass(), loader);
    }
    
    
    private static boolean fromPlatforms(Class<?> annotated, Class<?> caller, BundleLoader loader) {
        var success = true;
        
        for (var platform : annotated.getAnnotationsByType(Platform.class)) {
            var locales = new ArrayList<Locale>(platform.locales().length);
            for (var locale : platform.locales()) {
                locales.add(Locales.of(locale));
            }
            
            var template = platform.template();
            if (!template.embedded().isEmpty()) {
                success &= fromClassLoader(template.embedded(), caller.getClassLoader(), locales, platform.destination(), loader);
                
            } else if (!template.system().isEmpty()) {
                success &= fromPlatform(new File(template.system()), locales, platform.destination(), loader);
                
            } else {
                throw new IllegalArgumentException("Invalid template, either an embedded or system template must be specified");
            }
        }
        
        return success;
    }
    
    
    
    public static boolean fromClassLoader(String file, Collection<Locale> locales, String destination) {
        return fromClassLoader(file, STACK.getCallerClass().getClassLoader(), locales, destination);
    }
    
    public static boolean fromClassLoader(String file, Collection<Locale> locales, String destination, BundleLoader loader) {
        return fromClassLoader(file, STACK.getCallerClass().getClassLoader(), locales, destination, loader);
    }
    
    
    public static boolean fromClassLoader(String file, ClassLoader loader, Collection<Locale> locales, String destination) {
        return fromClassLoader(file, loader, locales, destination, LOADER);
    }
    
    public static boolean fromClassLoader(String file, ClassLoader loader, Collection<Locale> locales, String destination, BundleLoader bundleLoader) {
        return from(new File(file), loader.getResourceAsStream(file), locales, destination, bundleLoader);
    }
    
    
    public static boolean fromModule(String file, Collection<Locale> locales, String destination) {
        return fromModule(file, STACK.getCallerClass().getModule(), locales, destination, LOADER);
    }
    
    public static boolean fromModule(String file, Collection<Locale> locales, String destination, BundleLoader loader) {
        return fromModule(file, STACK.getCallerClass().getModule(), locales, destination, loader);
    }
    
    
    public static boolean fromModule(String file, Module module, Collection<Locale> locales, String destination) {
        return fromModule(file, module, locales, destination, LOADER);
    }
    
    public static boolean fromModule(String file, Module module, Collection<Locale> locales, String destination, BundleLoader loader) {
        try {
            return from(new File(file), module.getResourceAsStream(file), locales, destination, loader);
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    
    public static boolean fromPlatform(File file, Collection<Locale> locales, String destination) {
        return fromPlatform(file, locales, destination, LOADER);
    }
    
    public static boolean fromPlatform(File file, Collection<Locale> locales, String destination, BundleLoader loader) {
        try {
            return from(file, new FileInputStream(file), locales, destination, loader);
            
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    
    public static boolean from(File file, InputStream stream, Collection<Locale> locales, String destination) {
        return from(file, stream, locales, destination, LOADER);
    }
    
    public static boolean from(File file, InputStream stream, Collection<Locale> locales, String destination, BundleLoader loader) {
        var segments = file.getName().split(".");
        if (segments.length < 2) {
            throw new IllegalArgumentException("Invalid file name, file name is missing an extension");
        }
        
        var name = segments[segments.length - 2];
        var format = segments[segments.length - 1];
        
        try (var in = stream.markSupported() ? stream : new BufferedInputStream(stream)) {
            boolean success = true;
            for (var locale : locales) {
                var bundle = loader.toResourceName(loader.toBundleName(name, locale), format);
                var target = Paths.get(destination, bundle);
                
                var creatable = Files.notExists(target);
                if (creatable) {
                    in.mark(Integer.MAX_VALUE);
                    Files.copy(in, target);
                    in.reset();
                }
                
                success &= creatable;
            }
            return success;
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
}
