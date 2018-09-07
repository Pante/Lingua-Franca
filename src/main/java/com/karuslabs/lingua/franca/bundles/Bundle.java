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

import java.text.MessageFormat;
import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;


public abstract class Bundle {
    
    private static final ThreadLocal<MessageFormat> FORMAT = new ThreadLocal<>() {
        @Override
        protected MessageFormat initialValue() {
            return new MessageFormat("");
        }
    };

    
    public abstract boolean contains(String key);
    
    public abstract Set<String> keys();
    
    
    public Optional<String> of(String key, Object... arguments) {
        return Optional.ofNullable(get(key, arguments));
    }
        
    public Optional<String> of(String key) {
        return Optional.ofNullable(get(key));
    }
        
    
    public @Nullable String get(String key, Object... arguments) {
        var message = get(key);
        if (message != null) {
            return format(message, arguments);
            
        } else {
            return null;
        }
    }
    
    public abstract @Nullable String get(String key);
    
    
    public Optional<String> in(String key, int index, Object... arguments) {
        return Optional.ofNullable(at(key, index, arguments));
    }
    
    public Optional<String> in(String key, int index) {
        return Optional.ofNullable(at(key, index));
    }
    
    public Optional<String[]> in(String key) {
        return Optional.ofNullable(at(key));
    }
    
    
    public @Nullable String at(String key, int index, Object... arguments) {
        var message = at(key, index);
        if (message != null) {
            return format(message, arguments);
            
        } else {
            return null;
        }
    }
    
    public @Nullable String at(String key, int index) {
        return at(key)[index];
    }
    
    public abstract @Nullable String[] at(String key);
    
    
    protected String format(String message, Object... arguments) {
        var format = FORMAT.get();
        format.setLocale(locale());
        format.applyPattern(message);
        return format.format(arguments);
    }
    
    
    public abstract Locale locale();
    
}
