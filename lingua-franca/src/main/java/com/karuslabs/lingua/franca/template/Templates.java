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
    
    private static final ResourceBundle.Control CONTROL = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT);
    private static final StackWalker STACK = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
    
    
    public static boolean fromPlatforms(Object annotated) {
        return fromPlatforms(annotated.getClass(), STACK.getCallerClass());
    }
     
    public static boolean fromPlatforms(Class<?> annotated) {
        return fromPlatforms(annotated, STACK.getCallerClass());
    }
    
    public static boolean fromPlatforms(Class<?> annotated, Class<?> caller) {
        var success = true;
        
        for (var platform : annotated.getAnnotationsByType(Platform.class)) {
            success &= fromPlatforms(platform, caller);
        }
        
        return success;
    }
    
    public static boolean fromPlatforms(Platform platform, Class<?> caller) {
        var locales = new ArrayList<Locale>(platform.locales().length);
        for (var locale : platform.locales()) {
            locales.add(Locales.of(locale));
        }

        var template = platform.template();
        if (!template.embedded().isEmpty()) {
            return fromClassLoader(template.embedded(), caller.getClassLoader(), locales, platform.destination());

        } else if (!template.system().isEmpty()) {
            return from(new File(template.system()), locales, platform.destination());

        } else {
            throw new IllegalArgumentException("Invalid template, either an embedded or system template must be specified");
        }
    }

    
    public static boolean fromClassLoader(String source, Collection<Locale> locales, String destination) {
        return fromClassLoader(source, STACK.getCallerClass().getClassLoader(), locales, destination);
    }
    
    public static boolean fromClassLoader(String source, ClassLoader loader, Collection<Locale> locales, String destination) {
        return from(new File(source), loader.getResourceAsStream(source), locales, destination);
    }
    
    
    public static boolean fromModule(String source, Collection<Locale> locales, String destination) {
        return fromModule(source, STACK.getCallerClass().getModule(), locales, destination);
    }
    
    public static boolean fromModule(String source, Module module, Collection<Locale> locales, String destination) {
        try {
            return from(new File(source), module.getResourceAsStream(source), locales, destination);
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    
    public static boolean from(File source, Collection<Locale> locales, String destination) {
        try {
            return from(source, new FileInputStream(source), locales, destination);
            
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    
    public static boolean from(File source, InputStream stream, Collection<Locale> locales, String destination) {
        var name = source.getName();
        int index = name.lastIndexOf('.');
        if (index <= 0) {
            throw new IllegalArgumentException("Invalid file name, file name is either blank or missing an extension");
        }
        
        return from(name.substring(0, index), name.substring(index + 1), stream, locales, destination);
    }
    
    public static boolean from(String name, String format, InputStream stream, Collection<Locale> locales, String destination) {
        try (var in = stream.markSupported() ? stream : new BufferedInputStream(stream)) {
            boolean success = true;
            for (var locale : locales) {
                var bundle = CONTROL.toResourceName(CONTROL.toBundleName(name, locale), format);
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
