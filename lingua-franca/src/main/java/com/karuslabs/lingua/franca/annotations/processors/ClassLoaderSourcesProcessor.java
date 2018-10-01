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

import com.karuslabs.lingua.franca.annotations.ClassLoaderSources;

import java.net.URL;
import javax.annotation.processing.*;
import javax.lang.model.element.*;

import static javax.lang.model.SourceVersion.RELEASE_10;


@SupportedSourceVersion(RELEASE_10)
@SupportedAnnotationTypes({
    "com.karuslabs.lingua.franca.annotations.ClassLoaderSources"
})
public class ClassLoaderSourcesProcessor extends SourcesProcessor {

    public ClassLoaderSourcesProcessor() {
        super("ClassLoaderSources");
    }

    
    @Override
    protected String[] directories(Element element) {
        return element.getAnnotation(ClassLoaderSources.class).value();
    }

    @Override
    protected URL find(String file) {
        var url = getClass().getClassLoader().getResource(getClass().getName().replace('.', '/') + ".class");
        System.out.println(url);
        return url;
    }
    
}
