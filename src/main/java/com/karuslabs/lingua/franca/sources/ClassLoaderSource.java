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


public class ClassLoaderSource extends FileSource {
    
    private ClassLoader loader;
    
    
    public ClassLoaderSource(String folder) {
        super(folder);
        this.loader = getClass().getClassLoader();
    }
    
    public ClassLoaderSource(ClassLoader loader, String folder) {
        super(folder);
        this.loader = loader;
    }

    
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
        return String.format(getClass().getName() + "[classloader = %s, folder = %s]", loader, folder);
    }
    
}
