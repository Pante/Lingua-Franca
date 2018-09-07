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
package com.karuslabs.lingua.franca.bundles;

import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;


public class LegacyBundle extends Bundle {
    
    private static final String[] EMPTY = new String[] {};
    
    private ResourceBundle bundle;
    private volatile @Nullable Set<String> keys;
    
    
    public LegacyBundle(ResourceBundle bundle) {
        this.bundle = bundle;
        this.keys = null;
    }
    
    
    @Override
    public boolean contains(String key) {
        return bundle.containsKey(key);
    }

    @Override
    public Set<String> keys() {
        if (keys == null) {
            keys = Set.of(Collections.list(bundle.getKeys()).toArray(EMPTY));
        }
        
        return keys;
    }
    
    
    @Override
    public String get(String key) {
        return bundle.getString(key);
    }

    @Override
    public String[] at(String key) {
        return bundle.getStringArray(key);
    }

    @Override
    public Locale locale() {
        return bundle.getLocale();
    }
    
}
