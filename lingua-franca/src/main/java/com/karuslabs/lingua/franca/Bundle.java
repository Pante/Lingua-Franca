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


/**
 * Bundles contain localised messages and facilities for formatting those messages.
 * Similar to {@code ResourceBundle}s in the JDK, bundles belong to families whose 
 * members share a common base name. Bundles should be obtained through a {@link Bundler}
 * and should not be created directly.
 * <p>
 * The default bundle implementation is thread-safe and non-blocking. Keys in subsequent 
 * levels are delimited by a full-stop, i.e. {@code path.to.value}. In addition, messages 
 * in arrays can be accessed via enclosing the message index in square brackets, 
 * i.e. {@code path.to.array.value[i]}.

 * Retrieval operations attempt to retrieve a message from a bundle and its parents 
 * recursively until the root bundle is reached. If a message is unable to be retrieved,
 * either an empty {@code Optional} or {@code null} is returned.
 */
public class Bundle {
    
    /**
     * Represents an empty bundle.
     */
    public static final Bundle EMPTY;
    
    static {
        EMPTY = new EmptyBundle(Locale.ROOT, null);
        EMPTY.parent = EMPTY;
    }
    
    /**
     * Creates an empty bundle with the specified locale and parent.
     * Returns {@link Bundle#EMPTY} if the specified locale is the root locale.
     * 
     * @param locale the locale of the empty bundle
     * @param parent the parent of the empty bundle
     * @return an empty bundle
     */
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
    
    
    /**
     * Creates a {@code Bundle} with the specified messages and locale and no parent bundle.
     * 
     * @param messages the messages
     * @param locale the locale
     */
    public Bundle(ConcurrentMap<String, Object> messages, Locale locale) {
        this(messages, locale, Bundle.EMPTY);
    }
    
    /**
     * Creates a {@code Bundle} with the specified messages, locale and parent.
     * 
     * @param messages the messages
     * @param locale the locale
     * @param parent the parent of this bundle
     */
    public Bundle(ConcurrentMap<String, Object> messages, Locale locale, Bundle parent) {
        this.messages = messages;
        this.keys = null;
        this.locale = locale;
        this.parent = parent;
        this.hash = 0;
    }

    
    /**
     * Retrieves the message associated with the specified key.
     * 
     * @param key the key whose associated message is to be retrieved
     * @return the message to which the specified key is mapped, or an empty optional 
     *         if this bundle contains no mapping for the key
     */
    public Optional<String> get(String key) {
        var message = retrieve(key);
        return message instanceof String ? Optional.of((String) message) : EMPTY_STRING;
    }
    
    /**
     * Formats the message associated with the specified key using the specified arguments.
     * 
     * @param key the key whose associated message is to be formatted
     * @param arguments the arguments used to format the message
     * @return the formatted message to which the specified key is map, or an empty 
     *         optional if this bundle contains no mapping for the key
     */
    public Optional<String> get(String key, Object... arguments) {
        var message = retrieve(key);
        return message instanceof String ? Optional.of(format((String) message, arguments)) : EMPTY_STRING;
    }
    
    
    /**
     * Retrieves the message associated with the specified key.
     * 
     * @param key the key whose associated message is to be retrieved
     * @return the message to which the specified key is map, or null if this bundle
     *         contains no mapping for the key
     */
    public @Nullable String find(String key) {
        var message = retrieve(key);
        return message instanceof String ? (String) message : null;
    }
    
    /**
     * Formats the message associated with the specified key.
     * 
     * @param key the key whose associated message is to be formatted
     * @param arguments the arguments used to format the message
     * @return the formatted message to which the specified key is map, or null if
     *         this bundle contains no mapping for the key
     */
    public @Nullable String find(String key, Object... arguments) {
        var message = retrieve(key);
        return message instanceof String ? format((String) message, arguments) : null;
    }
    
    
    /**
     * Retrieves the messages associated with the specified key.
     * 
     * @param key the key whose associated messages is to be retrieved
     * @return the messages to which the specified key is mapped, or an empty optional
     *         if this bundle contains no mapping for the key
     */
    public Optional<String[]> messages(String key) {
        var messages = retrieve(key);
        return messages instanceof String[] ? Optional.of((String[]) messages) : EMPTY_ARRAY;
    }
    
    /**
     * Retrieves the messages associated with the specified key.
     * 
     * @param key the key whose associated messages is to be retrieved
     * @return the messages to which the specified key is map, or null if this bundle
     *         contains no mapping for the key
     */
    public @Nullable String[] messagesIfPresent(String key) {
        var messages = retrieve(key);
        return messages instanceof String[] ? (String[]) messages : null;
    }
    
    
    /**
     * Recursively retrieves the value associated with the specified key from this 
     * bundle and its parents.
     * <p>
     * Caches values retrieved from its parent to improve subsequent look-up performance
     * from {@code O(n)} to {@code O(1)}.
     * 
     * @param key the key whose associated value is to be retrieved
     * @return the value to which the specified key is mapped, or null if this bundle
     *         and its parents contains no mapping for the key
     */
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
    
    /**
     * Formats the message using the specified arguments the locale of this bundle.
     * 
     * @param message the message to be formatted
     * @param arguments the arguments used to format the message
     * @return the formatted message
     */
    protected String format(String message, Object... arguments) {
        var formatter = FORMATTER.get();
        formatter.setLocale(locale);
        formatter.applyPattern(message);
        return formatter.format(arguments);
    }
    
    
    /**
     * Recursively retrieves the keys of this bundle and its parents.
     * 
     * @return the keys of this Bundle and its parents
     */
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
    
    /**
     * Returns the locale of this bundle.
     * 
     * @return the locale
     */
    public Locale locale() {
        return locale;
    }
    
    /**
     * Returns the parent of this bundle.
     * 
     * @return the parent of this bundle
     */
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