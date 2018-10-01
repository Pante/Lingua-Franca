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
package com.karuslabs.lingua.franca.annotations.processors;

import java.net.URL;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.element.*;

import static javax.tools.Diagnostic.Kind.*;
import org.checkerframework.checker.nullness.qual.Nullable;


public abstract class SourcesProcessor extends AnnotationProcessor {
    
    private String annotation;
    
    
    public SourcesProcessor(String annotation) {
        this.annotation = annotation;
    }
    
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        for (var element : types(annotations, environment))  {
            process(directories(element), element);
        }
        return false;
    }
    
    protected void process(String[] directories, Element element) {
        if (directories.length == 0) {
            messager.printMessage(ERROR, "Empty @" + annotation + " annotated type, " + element.asType().toString() + ", sources cannot be empty", element);
        }
        
        for (var directory : directories) {
            if (find(directory) == null) {
                messager.printMessage(ERROR, "Invalid directory, " + directory + " for @" + annotation + " annotated type, " + element.asType().toString() + ", directory must exist and be a directory", element);
            }
        }
    }
    
    protected abstract String[] directories(Element element);
    
    protected abstract @Nullable URL find(String file);
    
}
