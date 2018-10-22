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

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.Nullable;


public class Bundle {
    
    public static final Bundle EMPTY;
    
    static {
        EMPTY = new EmptyBundle(Locale.ROOT, null);
        EMPTY.parent = EMPTY;
    }
    
    public static Bundle empty(Locale locale, Bundle parent) {
        return Locale.ROOT.equals(locale) ?  EMPTY : new EmptyBundle(locale, parent);
    }

    
    private static final ThreadLocal<MessageFormat> FORMATTER = new ThreadLocal<>() {
        @Override
        public MessageFormat initialValue() {
            return new MessageFormat("");
        }
    };
        
    protected static final Optional<String> EMPTY_STRING = Optional.empty();
    protected static final Optional<String[]> EMPTY_ARRAY = Optional.empty();
    
    
    protected ConcurrentMap<String, Object> messages;
    private volatile @Nullable Set<String> keys;
    private final Locale locale;
    protected Bundle parent;
    private volatile int hash;
    
    
    public Bundle(ConcurrentMap<String, Object> messages, Locale locale) {
        this(messages, locale, Bundle.EMPTY);
    }
    
    public Bundle(ConcurrentMap<String, Object> messages, Locale locale, Bundle parent) {
        this.messages = messages;
        this.keys = null;
        this.locale = locale;
        this.parent = parent;
        this.hash = 0;
    }

    
    public Optional<String> get(String key) {
        var message = retrieve(key);
        return message instanceof String ? Optional.of((String) message) : EMPTY_STRING;
    }
    
    public Optional<String> get(String key, Object... arguments) {
        var message = retrieve(key);
        return message instanceof String ? Optional.of(format((String) message, arguments)) : EMPTY_STRING;
    }
    
    
    public @Nullable String find(String key) {
        var message = retrieve(key);
        return message instanceof String ? (String) message : null;
    }
    
    public @Nullable String find(String key, Object... arguments) {
        var message = retrieve(key);
        return message instanceof String ? format((String) message, arguments) : null;
    }
    

    public Optional<String[]> messages(String key) {
        var messages = retrieve(key);
        return messages instanceof String[] ? Optional.of((String[]) messages) : EMPTY_ARRAY;
    }
    
    public @Nullable String[] messagesIfPresent(String key) {
        var messages = retrieve(key);
        return messages instanceof String[] ? (String[]) messages : null;
    }
    
    
    protected @Nullable Object retrieve(String key) {
        var message = messages.get(key);
        if (message == null) {
            message = parent.retrieve(key);
            if (message != null) {
                messages.put(key, message);
            }
        }
        
        return message;
    }
    
    protected String format(String message, Object... arguments) {
        var formatter = FORMATTER.get();
        formatter.setLocale(locale);
        formatter.applyPattern(message);
        return formatter.format(arguments);
    }
    
    
    public Set<String> keys() {
        var set = keys;
        if (set == null) {
            synchronized (EMPTY_ARRAY) {
                set = keys;
                if (set == null) {
                    set = new HashSet<>(parent.keys());
                    for (var entry : messages.entrySet()) {
                        if (entry.getValue() instanceof String) {
                            set.add(entry.getKey());
                        }
                    }
                    this.keys = set = Collections.unmodifiableSet(set);
                }
            }
        }
        
        return set;
    }
    
    public Locale locale() {
        return locale;
    }
    
    public Bundle parent() {
        return parent;
    }
    
    
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Bundle && locale.equals(((Bundle) other).locale);
    }

    @Override
    public int hashCode() {
        int value = hash;
        if (value == 0) {
            value = 5;
            value = 53 * value + getClass().hashCode();
            value = 53 * value + locale.hashCode();
            hash = value;
        }
        return value;
    }
    
    
    @Override
    public String toString() {
        return String.format("%s[locale = %s, parent locale = %s]", getClass().getName(), locale().toString(), parent.locale().toString());
    }
    
}