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

package com.karuslabs.lingua.maven.plugin.lint;

import com.karuslabs.lingua.franca.annotations.ClassLoaderSources;

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
@ClassLoaderSources({})
class LinguaLintMojoTest {
    
    LinguaLintMojo mojo = spy(new LinguaLintMojo());
    Log logger = mock(Log.class);
    
    
    @BeforeEach
    void before() {
        mojo.elements = List.of("");
        doReturn(logger).when(mojo).getLog();
    }
    
    
    
    @Test
    void execute() throws MojoExecutionException, MojoFailureException {
        Reflections reflection = when(mock(Reflections.class).getTypesAnnotatedWith(any(Class.class))).thenReturn(new HashSet<>()).getMock();
        doReturn(reflection).when(mojo).reflection();
        
        mojo.execute();
        
        verify(logger).info("Compile classpaths for project detected - analyzing project");
        verify(logger).info("Analysis completed successfully");
    }
    
    
    @Test
    void execute_exception() throws MojoExecutionException, MojoFailureException {
        assertEquals("Analysis completed - detected potential issues", assertThrows(MojoFailureException.class, mojo::execute).getMessage());
        verify(logger).info("Compile classpaths for project detected - analyzing project");
    }
    
}
