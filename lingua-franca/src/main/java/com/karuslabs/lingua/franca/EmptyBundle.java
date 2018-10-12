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

import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * Represents an empty bundle.
 */
public class EmptyBundle extends Bundle {    
    
    /**
     * Creates an {@code EmptyBundle} with the specified locale and parent bundle.
     * 
     * @param locale the locale
     * @param parent the parent bundle
     */
    public EmptyBundle(Locale locale, Bundle parent) {
        super(Map.of(), locale, parent);
    }
    
    
    /**
     * Always returns {@code null}.
     * 
     * @param key the key
     * @return null
     */
    @Override
    public @Nullable String find(String key) {
        return null;
    }
 
    /**
     * Always returns {@code null}.
     * 
     * @param key the key
     * @param arguments the arguments
     * @return null
     */
    @Override
    public @Nullable String find(String key, Object... arguments) {
        return null;
    }
    
    
    /**
     * Always returns an empty optional.
     * 
     * @param key the key
     * @return an empty optional
     */
    @Override
    public Optional<String> get(String key) {
        return EMPTY_STRING;
    }
    
    /**
     * Always returns an empty optional.
     * 
     * @param key the key
     * @param arguments the arguments
     * @return an empty optional
     */
    @Override
    public Optional<String> get(String key, Object... arguments) {
        return EMPTY_STRING;
    }
    
    
    /**
     * Always returns an empty optional.
     * 
     * @param key the key
     * @return an empty optional
     */
    @Override
    public Optional<String[]> messages(String key) {
        return EMPTY_ARRAY;
    }
    
    /**
     * Always returns null.
     * 
     * @param key the key
     * @return null
     */
    @Override
    public @Nullable String[] messagesIfPresent(String key) {
        return null;
    }
    
    /**
     * Always returns null.
     * 
     * @param key the key
     * @return null
     */
    @Override
    protected @Nullable Object retrieve(String key) {
        return null;
    }
    
    /**
     * Always returns an immutable empty set.
     * 
     * @return an immutable empty set
     */
    @Override
    public Set<String> keys() {
        return Collections.emptySet();
    }
    
}