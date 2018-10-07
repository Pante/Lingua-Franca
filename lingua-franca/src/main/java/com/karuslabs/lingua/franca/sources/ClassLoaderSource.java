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
package com.karuslabs.lingua.franca.sources;

import java.io.InputStream;

import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * A {@code Source} implementation from which file resources relative to a {@code ClassLoader}
 * can be retrieved using the {@code ClassLoader}.
 * <p>
 * {@code ClassLoaderSource}s can be used to retrieve embedded resources.
 */
public class ClassLoaderSource extends FileSource {
    
    /**
     * The source for the root directory relative to the {@code ClassLoader}.
     */
    public static final ClassLoaderSource ROOT = new ClassLoaderSource("");
    
    
    private final ClassLoader loader;
    
    
    /**
     * Creates a {@code ClassLoaderSource} with the specified folder using the {@code ClassLoader}
     * of the calling class.
     * 
     * This method is caller sensitive.
     * 
     * @param folder the folder
     */
    public ClassLoaderSource(String folder) {
        this(STACK.getCallerClass().getClassLoader(), folder);
    }
    
    /**
     * Creates a {@code ClassLoaderSource} with the specified {@code ClassLoader}
     * and folder.
     * 
     * @param loader the ClassLoader
     * @param folder the folder
     */
    public ClassLoaderSource(ClassLoader loader, String folder) {
        super(folder);
        this.loader = loader;
    }

    
    /**
     * Creates a stream for the specified resource relative to the {@code ClassLoader}.
     * 
     * @param resource the resource
     * @return a stream for the specified resource, or null if a stream could not be created
     */
    @Override
    public @Nullable InputStream load(String resource) {
        return loader.getResourceAsStream(folder + resource);
    }
    
    
    @Override
    public boolean equals(Object other) {
        return super.equals(other) && loader.equals(((ClassLoaderSource) other).loader);
    }
    
    @Override
    public int hashCode() {
        return 53 * super.hashCode() + loader.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("%s[classloader = %s, folder = %s]", getClass().getName(), loader, folder);
    }
    
}
