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

import com.karuslabs.lingua.franca.BundleLoader;

import java.io.*;
import java.lang.StackWalker.Option;
import java.nio.file.*;
import java.util.*;


public class Templates {
    
    private static final BundleLoader LOADER = BundleLoader.loader();
    private static final StackWalker STACK = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
    
    
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
        return from(file, loader.getResourceAsStream(file), locales, destination, bundleLoader);
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
            return from(file, module.getResourceAsStream(file), locales, destination, loader);
            
        } catch (IOException e) {
            return false;
        }
    }
    
    
    public static boolean fromSystem(String file, Collection<Locale> locales, String destination) {
        return fromSystem(file, locales, destination, LOADER);
    }
    
    public static boolean fromSystem(String file, Collection<Locale> locales, String destination, BundleLoader loader) {
        try {
            return from(file, new FileInputStream(file), locales, destination, loader);
            
        } catch (FileNotFoundException e) {
            return false;
        }
    }
    
    
    public static boolean from(String file, InputStream stream, Collection<Locale> locales, String destination) {
        return from(file, stream, locales, destination, LOADER);
    }
    
    public static boolean from(String source, InputStream stream, Collection<Locale> locales, String destination, BundleLoader loader) {
        var segments = new File(source).getName().split(".");
        if (segments.length < 2) {
            return false;
        }
        
        var name = segments[segments.length - 2];
        var format = segments[segments.length - 1];
        
        try (var in = stream.markSupported() ? stream : new BufferedInputStream(stream)) {
            for (var locale : locales) {
                var bundle = loader.toResourceName(loader.toBundleName(name, locale), format);
                var file = Paths.get(destination, bundle);
                
                if (Files.notExists(file)) {
                    in.mark(Integer.MAX_VALUE);
                    Files.copy(in, file);
                    in.reset();
                }    
            }
            return true;
            
        } catch (IOException e) {
            return false;
        }
    }
    
}
