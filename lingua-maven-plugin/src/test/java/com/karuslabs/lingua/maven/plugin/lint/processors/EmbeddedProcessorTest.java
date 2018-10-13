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

import com.karuslabs.lingua.franca.template.annotations.Embedded;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@Embedded(template = "", locales = {}, destination = "")
class EmbeddedProcessorTest {
    
    EmbeddedProcessor processor;
    Log logger = mock(Log.class);
    
    
    @BeforeEach
    void before() throws URISyntaxException {
        var folder = new File(getClass().getClassLoader().getResource("folder/file.yml").toURI());
        processor = spy(new EmbeddedProcessor(folder.getParentFile()));
    }
    
    
    @Test
    void process() {
        doReturn(false).when(processor).processEmbedded(any(), any(), any(), any());
        doReturn(true).when(processor).processLocales(any(), any(), any(), any());
        doReturn(false).when(processor).processDestination(any(), any(), any(), any());
        
        assertFalse(processor.process(Set.of(EmbeddedProcessorTest.class), logger));
        verify(processor).processEmbedded(any(), any(), any(), any());
        verify(processor).processLocales(any(), any(), any(), any());
        verify(processor).processDestination(any(), any(), any(), any());
    }
    
    
    @Test
    void processDestiantion() {
        assertTrue(processor.processDestination(logger, getClass(), "Test", ""));
        verify(logger, times(0)).error(any(CharSequence.class));
    }
    
    
    @Test
    void processDestination_error() {
        assertFalse(processor.processDestination(logger, getClass(), "Test", "invalid.yml"));
        verify(logger).error("Invalid @Test annotation for " + getClass().getName() + ", 'invalid.yml' either does not exist or is not a folder");
    }
    
}
