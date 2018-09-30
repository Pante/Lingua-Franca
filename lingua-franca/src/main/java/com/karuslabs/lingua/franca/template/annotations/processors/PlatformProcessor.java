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
package com.karuslabs.lingua.franca.template.annotations.processors;

import com.karuslabs.lingua.franca.template.annotations.*;

import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.element.*;

import static javax.lang.model.SourceVersion.RELEASE_10;
import static javax.tools.Diagnostic.Kind.*;


@SupportedSourceVersion(RELEASE_10)
@SupportedAnnotationTypes({
    "com.karuslabs.lingua.franca.template.annotations.Platform",
    "com.karuslabs.lingua.franca.template.annotations.Platforms"
})
public class PlatformProcessor extends TemplateProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        for (var element : types(annotations, environment))  {
            for (var annotation : element.getAnnotationsByType(Platform.class)) {
                processPlatform(annotation.template(), element);
                processLocales("Platform", annotation.locales(), element);
            }
        }
        return false;
    }
    
    protected void processPlatform(In annotation, Element element) {
        if (annotation.embedded().isEmpty() && annotation.system().isEmpty()) {
            messager.printMessage(ERROR, "Invalid @Platform annotated type, " + element.asType().toString() + ", either an embedded or system template must be specified", element);
            
        } else if (!annotation.embedded().isEmpty() && !annotation.system().isEmpty()) {
            messager.printMessage(ERROR, "Invalid @Platform annotated type, " + element.asType().toString() + ", only a single template can be specified", element);
            
        } else {
            processEmbedded("Platform", "template", annotation.embedded(), element);
        }
    }
    
}
