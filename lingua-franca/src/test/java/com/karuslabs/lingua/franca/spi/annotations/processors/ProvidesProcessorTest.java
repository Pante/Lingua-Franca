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

package com.karuslabs.lingua.franca.spi.annotations.processors;

import com.karuslabs.lingua.franca.annotations.processors.*;
import com.karuslabs.lingua.franca.spi.AnnotatedBundleProvider;
import com.karuslabs.lingua.franca.spi.annotations.Provides;
import java.lang.annotation.Annotation;
import java.util.Set;

import java.util.stream.Stream;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static javax.tools.Diagnostic.Kind.ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@Provides({})
class ProvidesProcessorTest {
    
    ProvidesProcessor processor = spy(new ProvidesProcessor());
    Environment environment = new Environment();
    TypeMirror expected = mock(TypeMirror.class);
    TypeElement element = mock(TypeElement.class);
    
    
    @BeforeEach
    void before() {
        TypeElement e = when(mock(TypeElement.class).asType()).thenReturn(expected).getMock();
        when(environment.elements.getTypeElement(AnnotatedBundleProvider.class.getName())).thenReturn(e);
        doReturn(Set.of(element)).when(processor).types(any(), any());
        when(element.asType()).thenReturn(expected);
        when(element.getAnnotation(Provides.class)).thenReturn(ProvidesProcessorTest.class.getAnnotation(Provides.class));
        
        processor.init(environment);
    }
    
    
    @Test
    void annotations() throws ClassNotFoundException {
        AssertProcessor.annotations(ProvidesProcessor.class);
    }
    
    
    @Test
    void process() {
        when(environment.types.isAssignable(any(), eq(expected))).thenReturn(false);
        
        processor.process(Set.of(), environment);
        
        verify(environment.messager).printMessage(ERROR, "Invalid annotation target for @Provides, " + element.asType().toString() + " must extend " + expected.toString(), element);
        verify(environment.messager).printMessage(ERROR, "Invalid @Provides annotation for " + element.asType().toString() + ", @Provides must contain at least one bundle name", element);
    }
    
}
