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

import org.checkerframework.checker.nullness.qual.Nullable;


public class Bundle {
        
    public static final Bundle EMPTY = new EmptyBundle();
    
    
    private static final ThreadLocal<MessageFormat> FORMATTER = new ThreadLocal<>() {
        @Override
        public MessageFormat initialValue() {
            return new MessageFormat("");
        }
    };
        
    static final Optional<String> EMPTY_STRING = Optional.empty();
    static final Optional<String[]> EMPTY_ARRAY = Optional.empty();
    
    
    private Map<String, Object> messages;
    private Locale locale;
    private Bundle parent;
    private int hash;
    
    
    public Bundle(Map<String, Object> messages, Locale locale) {
        this(messages, locale, EMPTY);
    }
    
    public Bundle(Map<String, Object> messages, Locale locale, Bundle parent) {
        this.messages = messages;
        this.locale = locale;

        this.hash = 0;
    }
    
    
    public Optional<String> get(String key) {
        var message = find(key);
        return message instanceof String ? Optional.of((String) message) : EMPTY_STRING;
    }
    
    public Optional<String> get(String key, Object... arguments) {
        var message = find(key);
        return message instanceof String ? Optional.of(format((String) message, arguments)) : EMPTY_STRING;
    }
    
    public @Nullable String getIfPresent(String key) {
        var message = find(key);
        return message instanceof String ? (String) message : null;
    }
    
    public @Nullable String getIfPresent(String key, Object... arguments) {
        var message = find(key);
        return message instanceof String ? format((String) message, arguments) : null;
    }
    
    
    public Optional<String> at(String key, int index) {
        var message = find(key);
        return message instanceof String[] ? Optional.of(((String[]) message)[index]) : EMPTY_STRING;
    }
    
    public Optional<String> at(String key, int index, Object... arguments) {
        var message = find(key);
        return message instanceof String[] ? Optional.of(format(((String[]) message)[index], arguments)) : EMPTY_STRING;
    }
    
    public @Nullable String atIfPresent(String key, int index) {
        var message = find(key);
        return message instanceof String[] ? ((String[]) message)[index] : null;
    }
    
    public @Nullable String atIfPresent(String key, int index, Object... arguments) {
        var message = find(key);
        return message instanceof String[] ? format(((String[]) message)[index], arguments) : null;
    }
    
    
    public Optional<String[]> all(String key) {
        var message = find(key);
        return message instanceof String[] ? Optional.of((String[]) message) : EMPTY_ARRAY;
    }
    
    public @Nullable String[] allIfPresent(String key) {
        var message = find(key);
        return message instanceof String[] ? (String[]) message : null;
    }
    
    
    protected @Nullable Object find(String key) {
        var message = messages.get(key);
        if (message == null) {
            message = parent.find(key);
        }
        
        return message;
    }
    
    protected String format(String message, Object... arguments) {
        var formatter = FORMATTER.get();
        formatter.setLocale(locale);
        formatter.applyPattern(message);
        return formatter.format(arguments);
    }
    
    
    public Locale locale() {
        return locale;
    }
    
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        
        if (getClass() == null || getClass() != other.getClass()) {
            return false;
        }
        
        return locale.equals(((Bundle) other).locale);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            int calculated = 5;
            calculated = 53 * calculated + getClass().hashCode();
            calculated = 53 * calculated + locale.hashCode();
            hash = calculated;
        }
        
        return hash;
    }
    
}


class EmptyBundle extends Bundle {    
    
    EmptyBundle() {
        super(Map.of(), Locale.ROOT);
    }
    
    @Override
    public Optional<String> get(String key) {
        return EMPTY_STRING;
    }
    
    @Override
    public Optional<String> get(String key, Object... arguments) {
        return EMPTY_STRING;
    }
    
    @Override
    public @Nullable String getIfPresent(String key) {
        return null;
    }
 
    @Override
    public @Nullable String getIfPresent(String key, Object... arguments) {
        return null;
    }
    
    
    @Override
    public Optional<String> at(String key, int index) {
        return EMPTY_STRING;
    }
    
    @Override
    public Optional<String> at(String key, int index, Object... arguments) {
        return EMPTY_STRING;
    }
    
    @Override
    public @Nullable String atIfPresent(String key, int index) {
        return null;
    }
    
    @Override
    public @Nullable String atIfPresent(String key, int index, Object... arguments) {
        return null;
    }
    
    
    @Override
    public Optional<String[]> all(String key) {
        return EMPTY_ARRAY;
    }
    
    @Override
    public @Nullable String[] allIfPresent(String key) {
        return null;
    }
    
    
    @Override
    protected @Nullable Object find(String key) {
        return null;
    }
    
}
