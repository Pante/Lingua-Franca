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
import com.karuslabs.lingua.franca.sources.Source;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;


public class Templates {
    
    private BundleLoader loader;
    
    
    public Templates() {
        this(new BundleLoader());
    }
    
    public Templates(BundleLoader loader) {
        this.loader = loader;
    }
    
    
    public boolean create(Class<?> annotated) {
        
    }
    
    
    public boolean create(String name, Collection<Locale> locales, String folder) {
        return Templates.this.create(name, locales, folder, loader);
    }
    
    public boolean create(String name, Collection<Locale> locales, String folder, BundleLoader loader) {
        var sources = loader.namespaces().getOrDefault(name, loader.global());
        var formats = loader.formats();
        var bundleName = loader.toBundleName(name, Locale.ROOT);
        boolean created = false;
        
        if (!sources.isEmpty()) {
            created = create(sources, formats, name, bundleName, locales, folder, loader);
        }
         
        if (!created &&  sources != loader.global()) {
            created = create(sources, formats, name, bundleName, locales, folder, loader);
        }
        
        return created;
    }
    
    protected boolean create(Set<Source> sources, String[] formats, String name, String bundle, Collection<Locale> locales, String folder, BundleLoader loader) {
        for (var source : sources) {
            for (var format : formats) {
                var stream = source.load(loader.toResourceName(bundle, format));
                if (stream != null) {
                    return Templates.this.create(new BufferedInputStream(stream), name, locales, format, folder, loader);
                }
            }
        }
        
        return false;
    }
    
    protected boolean create(InputStream stream, String name, Collection<Locale> locales, String format, String folder, BundleLoader loader) {
        try (stream) {
            for (var locale : locales) {
                stream.mark(Integer.MAX_VALUE);
                
                var bundle = loader.toResourceName(loader.toBundleName(name, locale), format);
                Files.copy(stream, Paths.get(folder, bundle));
                
                stream.reset();
            }
            return true;
            
        } catch (IOException e) {
            return false;
        }
    }
    
}
