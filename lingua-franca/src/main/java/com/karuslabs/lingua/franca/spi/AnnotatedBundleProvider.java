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
package com.karuslabs.lingua.franca.spi;

import com.karuslabs.lingua.franca.spi.annotations.Provides;

import java.util.*;


/**
 * A {@code AnnotatedBundleProvider} can be annotated with a 
 * {@link com.karuslabs.lingua.franca.spi.annotations.Provides Provides} annotation
 * to specify the base names of bundles supported.
 */
public abstract class AnnotatedBundleProvider implements BundleProvider {
    
    private final Set<String> bundles;
    
    
    /**
     * Creates an {@code AnnotatedBundleProvider}.
     */
    protected AnnotatedBundleProvider() {
        var provided = getClass().getAnnotation(Provides.class);
        bundles = provided == null ? Set.of() : Set.of(provided.value());
    }
    
    
    /**
     * Determines if the {@code Provides} annotation contains the specified base name.
     * 
     * @param name the base name
     * @return true if the Provides annotation contains the specified base name
     * 
     * @see com.karuslabs.lingua.franca.spi.annotations.Provides
     */
    @Override
    public boolean provides(String name) {
        return bundles.contains(name);
    }
    
}
