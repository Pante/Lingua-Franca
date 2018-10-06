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

import com.google.common.cache.CacheBuilder;

import com.karuslabs.lingua.franca.sources.ClassLoaderSource;

import java.util.Locale;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class AssertTest {
    
    Bundler bundler = new Bundler(CacheBuilder.newBuilder().expireAfterAccess(10, MINUTES).maximumSize(512).build(), BundleLoader.loader());
    
    
    @BeforeEach
    void before() {
        bundler.loader().add(ClassLoaderSource.ROOT);
        bundler.loader().add(new ClassLoaderSource("assertion"));
    }
    
    
    @Test
    void subset_bundle_true() {
        assertTrue(Assert.subset(bundler.load("loaded", Locale.UK)));
    }
    
    
    @Test
    void subset_bundle_false() {
        assertFalse(Assert.subset(bundler.load("invalid", Locale.UK)));
    }
    
}
