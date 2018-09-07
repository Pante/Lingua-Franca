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

import java.lang.invoke.VarHandle;
import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;


public class MappedBundle extends Bundle {
    
    private Map<String, Object> messages;
    private Locale locale;
    private Bundle parent;
    private volatile @Nullable Set<String> keys;
    
    
    public MappedBundle(Map<String, Object> messages, Locale locale) {
        this(messages, locale, EmptyBundle.EMPTY);
    }
    
    public MappedBundle(Map<String, Object> messages, Locale locale, Bundle parent) {
        this.messages = messages;
        this.locale = locale;
        this.parent = parent;
        this.keys = null;
    }
    
    
    @Override
    public boolean contains(String key) {
        return messages.containsKey(key) || parent.contains(key);
    }

    @Override
    public Set<String> keys() {
        if (keys == null) {
            var set = new HashSet<>(messages.keySet());
            set.addAll(parent.keys());
            keys = set;
        }
        
        return keys;
    }
    
    @Override
    public @Nullable String get(String key) {
        var message = messages.get(key);
        if (message instanceof String) {
            return (String) message;
            
        } else {
            return null;
        }
    }

    @Override
    public @Nullable String[] at(String key) {
        var message = messages.get(key);
        if (message instanceof String[]) {
            return (String[]) message;
            
        } else {
            return null;
        }
    }

    @Override
    public Locale locale() {
        return locale;
    }
    
}
