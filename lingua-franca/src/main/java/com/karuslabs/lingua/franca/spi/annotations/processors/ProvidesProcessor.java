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

import com.karuslabs.lingua.franca.annotations.processors.AnnotationProcessor;
import com.karuslabs.lingua.franca.spi.AnnotatedBundleProvider;
import com.karuslabs.lingua.franca.spi.annotations.Provides;

import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.SourceVersion.RELEASE_10;
import static javax.tools.Diagnostic.Kind.*;


@SupportedSourceVersion(RELEASE_10)
@SupportedAnnotationTypes({
    "com.karuslabs.lingua.franca.spi.annotations.Provides"
})
public class ProvidesProcessor extends AnnotationProcessor {
    
    private TypeMirror expected;
    
    
    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        expected = environment.getElementUtils().getTypeElement(AnnotatedBundleProvider.class.getName()).asType();
    }
    
    
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        for (var element : types(annotations, environment))  {
            if (!types.isAssignable(element.asType(), expected)) {
                messager.printMessage(ERROR, "Invalid annotation target for @Provides, " + element.asType().toString() + " must extend " + expected.toString(), element);
            }
            
            var annotation = element.getAnnotation(Provides.class);
            if (annotation.value().length == 0) {
                messager.printMessage(ERROR, "Invalid @Provides annotation for " + element.asType().toString() + ", @Provides must contain at least one bundle name", element);
            }
        }
        
        return false;
    }
    
}
