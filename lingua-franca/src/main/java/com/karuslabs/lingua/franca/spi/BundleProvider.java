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

import com.karuslabs.lingua.franca.Bundle;

import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * A {@code BundleProvier} is a service provider interface for loading bundles.
 * {@link com.karuslabs.lingua.franca.Bundler Bundler} uses the deployed {@code BundleProvider}
 * to load bundles.
 */
public interface BundleProvider {
    
    /**
     * Loads a bundle with the specified base name and locale.
     * 
     * @param name the base name of the bundle
     * @param locale the locale of the bundle
     * @return the bundle, or null if the bundle could not be loaded
     */
    @Nullable Bundle get(String name, Locale locale);
    
    /**
     * Determines if this {@code BundleProvider} can provide bundles with the specified
     * base name.
     * 
     * @param name the base name
     * @return true if this BundleProvider can provide bundles with the specified base name
     */
    boolean provides(String name);
    
}
