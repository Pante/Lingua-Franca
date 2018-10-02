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

import java.util.Set;
import java.util.stream.Stream;

import org.apache.maven.plugin.logging.Log;

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
@Embedded(template = "folder/file.yml", locales = {"en_GB"}, destination = "folder")
class EmbededProcessorTest {
    
    EmbeddedProcessor processor = new EmbeddedProcessor();
    Log logger = mock(Log.class);
    
    
    @Embedded(template = "folder/.a", locales = {}, destination = "folder")
    static class Invalid {
        
    }
    
    
    @Test
    void process() {
        processor.process(Set.of(getClass()), logger);
        verify(logger).error("a");
        verify(logger).info("Files already exist for folder/file.yml in @Embedded annotation for " + getClass().getName());
    }
    
    
    @Test
    void process_exception() {
        assertFalse(processor.process(Set.of(Invalid.class), logger));
        verify(logger).error("Exception occured while generating file(s) for template: folder/.a in @Embeded annotation for " + Invalid.class.getName() + ": Invalid file name, file name is either blank or missing an extension");
    }
    
}
