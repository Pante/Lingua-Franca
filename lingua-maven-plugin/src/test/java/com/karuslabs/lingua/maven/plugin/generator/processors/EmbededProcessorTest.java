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
package com.karuslabs.lingua.maven.plugin.generator.processors;

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
class EmbededProcessorTest {
    
    EmbeddedProcessor processor;
    Log logger = mock(Log.class);
    
    
    @BeforeEach
    void before() throws URISyntaxException {
        var folder = new File(getClass().getClassLoader().getResource("folder/file.yml").toURI());
        processor = new EmbeddedProcessor(folder.getParentFile());
    }
    
    
    @Embedded(template = "file.yml", locales = "en_GB", destination = "")
    static class Valid {
        
    }
    
    
    @Embedded(template = ".a", locales = {}, destination = "")
    static class Invalid {
        
    }
    
    
    @Test
    void process() {
        processor.process(Set.of(Valid.class), logger);
        verify(logger).info("Files already exist for template, file.yml in @Embedded annotation for " + Valid.class.getName());
    }
    
    
    @Test
    void process_exception() {
        assertFalse(processor.process(Set.of(Invalid.class), logger));
        verify(logger).error(any(CharSequence.class));
    }
    
}
