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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BundleTest {
    
    static Bundle bundle = new Bundle(Stringifier.stringify().from(BundleTest.class.getClassLoader().getResourceAsStream("bundle.yml"), "yml"), Locale.ENGLISH);
    static final String value = "?";
    static final String[] empty = new String[] {};
    
    
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
    void getIfPresent() {
        assertEquals("b {0}", bundle.getIfPresent("key[1]"));
        assertNull(bundle.getIfPresent("key"));
    }
    
    
    @Test
    void getIfPresent_arguments() {
        assertEquals("b ?", bundle.getIfPresent("key[1]", value));
        assertNull(bundle.getIfPresent("key"));
    }
    
    
    @Test
    void at() {
        assertEquals("a {0}", bundle.at("key", 0).orElse(value));
        assertEquals(value, bundle.at("key", 2).orElse(value));
    }
    
    
    @Test
    void at_arguments() {
        assertEquals("a ?", bundle.at("key", 0, value).orElse(value));
        assertEquals(value, bundle.at("key", 2, value).orElse(value));
    }
    
    
    @Test
    void atIfPresent() {
        assertEquals("a {0}", bundle.atIfPresent("key", 0));
        assertNull(bundle.atIfPresent("key", 2));
    }
    
    
    @Test
    void atIfPresent_arguments() {
        assertEquals("b ?", bundle.atIfPresent("key", 1, value));
        assertNull(bundle.atIfPresent("key", 2, value));
    }
    
    
    @Test
    void all() {
        assertArrayEquals(new String[]{"a {0}", "b {0}"}, bundle.all("key").orElse(empty));
        assertArrayEquals(empty, bundle.all("other").orElse(empty));
    }
    
    
    @Test
    void allIfPresent() {
        assertArrayEquals(new String[]{"a {0}", "b {0}"}, bundle.allIfPresent("key"));
        assertNull(bundle.allIfPresent("other"));
    }
    
    
    @Test
    void find() {
        var bundle = new Bundle(Map.of(), Locale.ENGLISH, new Bundle(Map.of("key", "value"), Locale.ROOT));
        assertEquals("value", bundle.find("key"));
    }
    
    
    @Test
    void find_null() {
        assertNull(bundle.find("something"));
    }
    
    
    @Test
    void format_concurrency() throws InterruptedException, ExecutionException {
        var start = new CountDownLatch(2);
        var end = new CountDownLatch(3);
        
        var executor = Executors.newFixedThreadPool(2);
        
        var first = executor.submit(() -> {
            start.countDown();
            return bundle.getIfPresent("key[0]", "first");
        });
        
        var second = executor.submit(() -> {
            start.countDown();
            return bundle.getIfPresent("key[1]", "second");
        });
        
        end.countDown();
        
        assertEquals("a first", first.get());
        assertEquals("b second", second.get());
    }
    
}


@ExtendWith(MockitoExtension.class)
class EmptyBundleTest {
    
    
    
}
