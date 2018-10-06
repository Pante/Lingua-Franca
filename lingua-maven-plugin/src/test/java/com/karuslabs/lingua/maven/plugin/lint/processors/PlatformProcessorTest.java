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

import com.karuslabs.lingua.franca.template.annotations.*;

import java.util.Set;

import org.apache.maven.plugin.logging.Log;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PlatformProcessorTest {
    
    PlatformProcessor processor = spy(new PlatformProcessor(null));
    Log logger = mock(Log.class);
    
    
    @Platform(template = @In(embedded = "a"), locales = {}, destination = "")
    static class Valid  {
        
    }
    
    @Platform(template = @In, locales = {}, destination = "")
    static class Empty {
        
    }
    
    @Platform(template = @In(embedded = "a", system = "b"), locales = {}, destination = "")
    static class Multiple {
        
    }
    
    
    @BeforeEach
    void before() {
        doReturn(true).when(processor).processEmbedded(any(), any(), any(), any());
        doReturn(true).when(processor).processLocales(any(), any(), any(), any());
    }
    
    
    @Test
    void process_valid() {
        assertTrue(processor.process(Set.of(Valid.class), logger));
        verify(processor).processEmbedded(any(), any(), any(), any());
        verify(processor).processLocales(any(), any(), any(), any());
    }
    
    
    @Test
    void process_empty() {
        assertFalse(processor.process(Set.of(Empty.class), logger));
        
        logger.error("Invalid @Plaform annotation for " + Empty.class.getName() + ", @Platform must contain either an embeded or system template");
        
        verify(processor, times(0)).processEmbedded(any(), any(), any(), any());
        verify(processor).processLocales(any(), any(), any(), any());
    }
    
    
    @Test
    void process_multiple() {
        assertFalse(processor.process(Set.of(Empty.class), logger));
        
        logger.error("Invalid @Plaform annotation for " + Empty.class.getName() + ", @Platform must contain only a single template");
        
        verify(processor, times(0)).processEmbedded(any(), any(), any(), any());
        verify(processor).processLocales(any(), any(), any(), any());
    }
     
}
