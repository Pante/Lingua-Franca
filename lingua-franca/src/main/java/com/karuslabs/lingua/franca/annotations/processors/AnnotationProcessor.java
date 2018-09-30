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

import java.io.File;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.util.Types;

import static java.util.stream.Collectors.toSet;


public abstract class AnnotationProcessor extends AbstractProcessor {
    
    protected static final File RESOURCES = new File("./src/main/resources");
    
    protected Messager messager;
    protected Types types;
    
    
    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        messager = environment.getMessager();
        types = environment.getTypeUtils();
    }
    
        
    public Set<Element> types(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        return annotations.stream().map(environment::getElementsAnnotatedWith).flatMap(Collection::stream).collect(toSet());
    }
    
}
