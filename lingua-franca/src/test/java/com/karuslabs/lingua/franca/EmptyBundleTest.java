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

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class EmptyBundleTest {
    
    Bundle empty = Bundle.EMPTY;
    
    
    @Test
    void empty() {
        assertSame(empty, EmptyBundle.empty(Locale.ROOT, new Bundle(Map.of(), Locale.CANADA)));
        assertEquals(Locale.CHINESE, EmptyBundle.empty(Locale.CHINESE, empty).locale());
    }
    
    
    @Test
    void find() {
        assertNull(empty.find(""));
    }
    
    
    @Test
    void find_arguments() {
        assertNull(empty.find("", ""));
    }
    
    
    @Test
    void get() {
        assertSame(Optional.empty(), empty.get(""));
    }
    
    
    @Test
    void get_arguments() {
        assertSame(Optional.empty(), empty.get("", ""));
    }
    
    
    @Test
    void messages() {
        assertSame(Optional.empty(), empty.messages(""));
    }
    
    
    @Test
    void messagesIfPresent() {
        assertNull(empty.messagesIfPresent(""));
    }
    
    
    @Test
    void retrieve() {
        assertNull(empty.retrieve(""));
    }
    
    
    @Test
    void keys() {
        assertEquals(Collections.EMPTY_SET, empty.keys());
    }
    
}
