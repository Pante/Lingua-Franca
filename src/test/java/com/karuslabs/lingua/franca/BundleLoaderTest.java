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

import com.karuslabs.lingua.franca.sources.*;

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
class BundleLoaderTest {
    
    static BundleLoader cached = BundleLoader.loader();
    static Source named1 = new ClassLoaderSource("named1");
    static Source named2 = new ClassLoaderSource("named2");
    static Source unnamed1 = new ClassLoaderSource("unnamed3");
    static Source unnamed2 = new ClassLoaderSource("unnamed4");
    
    static {
        cached.register("named", named1, named2);
        cached.register(unnamed1, unnamed2);
    }
    
    BundleLoader loader = new BundleLoader();
    
    
    @Test
    void contains_global() {
        assertFalse(cached.contains(named1));
        assertTrue(cached.contains(unnamed1));
    }
        
    
    @Test
    void contains_globals() {
        assertFalse(cached.contains(named1, unnamed1));
        assertTrue(cached.contains(unnamed1, unnamed2));
    }
    
}
