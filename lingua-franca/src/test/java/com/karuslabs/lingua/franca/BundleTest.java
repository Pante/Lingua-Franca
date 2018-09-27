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

import com.karuslabs.lingua.franca.codec.Stringifier;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.of;


@ExtendWith(MockitoExtension.class)
class BundleTest {
    
    static Bundle bundle = new Bundle(Stringifier.stringify().from(BundleTest.class.getClassLoader().getResourceAsStream("bundle.yml"), "yml"), Locale.ENGLISH);
    static Bundle chained = new Bundle(Map.of("array", new String[] {"a"}, "array[0]", "a"), Locale.ENGLISH, new Bundle(Map.of("key", "value"), Locale.ROOT));
    static final String value = "?";
    static final String[] empty = new String[] {};
    
    
    @Test
    void empty() {
        assertSame(Bundle.EMPTY, EmptyBundle.empty(Locale.ROOT, new Bundle(Map.of(), Locale.CANADA)));
        assertEquals(Locale.CHINESE, EmptyBundle.empty(Locale.CHINESE, Bundle.EMPTY).locale());
    }
    
    
    @Test
    void get() {
        assertEquals("a {0}", bundle.get("key[0]").orElse(value));
        assertEquals(value, bundle.get("key").orElse(value));
    }
    
    
    @Test
    void get_arguments() {
        assertEquals("a ?", bundle.get("key[0]", value).orElse(value));
        assertEquals(value, bundle.get("key", value).orElse(value));
    }
    
    
    @Test
    void find() {
        assertEquals("b {0}", bundle.find("key[1]"));
        assertNull(bundle.find("key"));
    }
    
    
    @Test
    void find_arguments() {
        assertEquals("b ?", bundle.find("key[1]", value));
        assertNull(bundle.find("key"));
    }
    
    
    @Test
    void messages() {
        assertArrayEquals(new String[]{"a {0}", "b {0}"}, bundle.messages("key").orElse(empty));
        assertArrayEquals(empty, bundle.messages("other").orElse(empty));
    }
    
    
    @Test
    void messagesIfPresent() {
        assertArrayEquals(new String[]{"a {0}", "b {0}"}, bundle.messagesIfPresent("key"));
        assertNull(bundle.messagesIfPresent("other"));
    }
    
    
    @Test
    void retrieve() {
        assertEquals("value", chained.retrieve("key"));
    }
    
    
    @Test
    void retrieve_null() {
        assertNull(bundle.retrieve("something"));
    }
    
    
    @Test
    void format_concurrency() throws InterruptedException, ExecutionException {
        var start = new CountDownLatch(2);
        
        var executor = Executors.newFixedThreadPool(2);
        
        var first = executor.submit(() -> {
            start.countDown();
            start.await();
            return bundle.find("key[0]", "first");
        });
        
        var second = executor.submit(() -> {
            start.countDown();
            start.await();
            return bundle.find("key[1]", "second");
        });
        
        assertEquals("a first", first.get());
        assertEquals("b second", second.get());
    }
    
    
    @Test
    void keys() {
        var keys = chained.keys();
        assertEquals(2, keys.size());
        
        assertTrue(keys.containsAll(Set.of("array[0]", "key")));
    }
    
    
    @Test
    void getters() {
        assertEquals(Locale.ENGLISH, bundle.locale());
        assertSame(Bundle.EMPTY, bundle.parent());
    }
    
    
    @ParameterizedTest
    @MethodSource({"equality_provider"})
    void equals(Bundle other, boolean expected) {
        assertEquals(expected, bundle.equals(other));
    }
    
    
    @ParameterizedTest
    @MethodSource({"equality_provider"})
    void hashCode(Bundle other, boolean expected) {
        assertEquals(expected, bundle.hashCode() == other.hashCode());
    }
    
    
    static Stream<Arguments> equality_provider() {
        return Stream.of(
            of(bundle, true),
            of(new Bundle(Map.of(), Locale.ENGLISH), true),
            of(Bundle.EMPTY, false)
        );
    }
    
    
    @Test
    void bundle_toString() {
        var message = String.format(bundle.getClass().getName() + "[locale = %s, parent locale = %s]", bundle.locale().toString(), bundle.parent().locale().toString());
        assertEquals(message, bundle.toString());
    }
    
}