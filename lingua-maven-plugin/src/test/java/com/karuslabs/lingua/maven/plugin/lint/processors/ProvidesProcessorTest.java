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

package com.karuslabs.lingua.maven.plugin.lint.processors;

import com.karuslabs.lingua.franca.Bundle;
import com.karuslabs.lingua.franca.spi.AnnotatedBundleProvider;
import com.karuslabs.lingua.franca.spi.annotations.Provides;

import java.util.Locale;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProvidesProcessorTest {
    
    ProvidesProcessor processor = spy(new ProvidesProcessor());
    Log logger = mock(Log.class);
    
    
    @Provides({"a"})
    static class Valid extends AnnotatedBundleProvider {

        @Override
        public Bundle get(String name, Locale locale) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    } 
    
    @Provides({})
    static class Empty extends AnnotatedBundleProvider {

        @Override
        public Bundle get(String name, Locale locale) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    @Provides({"b"})
    static class Type {
        
    }
    
    
    @Test
    void process_valid() {
        assertTrue(processor.process(Set.of(Valid.class), logger));
        verify(logger, times(0)).error(any(CharSequence.class));
    }
    
    
    @Test
    void process_empty() {
        assertFalse(processor.process(Set.of(Empty.class), logger));
        verify(logger).error("Invalid @Provides annotation for " + Empty.class.getName() + ", @Provides must contain at least one bundle name");
    }
    
    
    @Test
    void process_type() {
        assertFalse(processor.process(Set.of(Type.class), logger));
        verify(logger).error("Invalid annotation target for @Provides, " + Type.class.getName() + " must extend " + AnnotatedBundleProvider.class.getName());
    }
    
}
