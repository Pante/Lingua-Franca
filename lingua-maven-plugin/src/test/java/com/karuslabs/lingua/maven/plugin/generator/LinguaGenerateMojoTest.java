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
package com.karuslabs.lingua.maven.plugin.generator;

import com.karuslabs.lingua.franca.template.annotations.Embedded;
import com.karuslabs.lingua.maven.plugin.generator.processors.EmbeddedProcessor;

import java.util.*;

import org.apache.maven.plugin.*;
import org.apache.maven.plugin.logging.Log;

import org.reflections.Reflections;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LinguaGenerateMojoTest {
    
    final LinguaGenerateMojo mojo = spy(new LinguaGenerateMojo());
    final Log logger = mock(Log.class);
    
    
    @BeforeEach
    void before() {
        mojo.elements = List.of("");
        doReturn(logger).when(mojo).getLog();
    }
    
    
    @Test
    void execute_success() throws MojoExecutionException, MojoFailureException {
        Reflections reflection = when(mock(Reflections.class).getTypesAnnotatedWith(Embedded.class)).thenReturn(Set.of()).getMock();
        doReturn(reflection).when(mojo).reflection();
        
        mojo.execute();
        
        verify(logger).info("Compile classpaths for project detected - generating locale files");
        verify(logger).info("Generation completed successfully");
    }
    
    
    @Test
    void execute_exception() throws MojoExecutionException, MojoFailureException {
        EmbeddedProcessor processor = when(mock(EmbeddedProcessor.class).process(any(), any())).thenReturn(false).getMock();
        doReturn(processor).when(mojo).processor();
        
        assertEquals("Generation completed - failed to generate files.", assertThrows(MojoFailureException.class, mojo::execute).getMessage());
    }
    
}
