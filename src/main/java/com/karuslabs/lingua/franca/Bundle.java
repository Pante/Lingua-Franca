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
    
    public static final Bundle EMPTY = new Bundle(Map.of(), Locale.ROOT);
    
    
    private static final ThreadLocal<MessageFormat> FORMATTER = new ThreadLocal<>() {
        @Override
        public MessageFormat initialValue() {
            return new MessageFormat("");
        }
    };
    
    
    private Map<String, Object> messages;
    private Locale locale;
    private int hash;
    
    
    public Bundle(Map<String, Object> messages, Locale locale) {
        this.messages = messages;
        this.locale = locale;
        this.hash = 0;
    }
    
    
    public Optional<String> get(String key) {
        var message = messages.get(key);
        return message instanceof String ? Optional.of((String) message) : Optional.empty();
    }
    
    public Optional<String> get(String key, Object... arguments) {
        var message = messages.get(key);
        return message instanceof String ? Optional.of(format((String) message, arguments)) : Optional.empty();
    }
    
    public @Nullable String getIfPresent(String key) {
        var message = messages.get(key);
        return message instanceof String ? (String) message : null;
    }
    
    public @Nullable String getIfPresent(String key, Object... arguments) {
        var message = messages.get(key);
        return message instanceof String ? format((String) message, arguments) : null;
    }
    
    
    public Optional<String> at(String key, int index) {
        var message = messages.get(key);
        return message instanceof String[] ? Optional.of(((String[]) message)[index]) : Optional.empty();
    }
    
    public Optional<String> at(String key, int index, Object... arguments) {
        var message = messages.get(key);
        return message instanceof String[] ? Optional.of(format(((String[]) message)[index], arguments)) : Optional.empty();
    }
    
    public @Nullable String atIfPresent(String key, int index) {
        var message = messages.get(key);
        return message instanceof String[] ? ((String[]) message)[index] : null;
    }
    
    public @Nullable String atIfPresent(String key, int index, Object... arguments) {
        var message = messages.get(key);
        return message instanceof String[] ? format(((String[]) message)[index], arguments) : null;
    }
    
    
    public Optional<String[]> all(String key) {
        var message = messages.get(key);
        return message instanceof String[] ? Optional.of((String[]) message) : Optional.empty();
    }
    
    public @Nullable String[] allIfPresent(String key) {
        var message = messages.get(key);
        return message instanceof String[] ? (String[]) message : null;
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
