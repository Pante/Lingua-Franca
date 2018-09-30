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

import com.karuslabs.lingua.franca.Locales;
import com.karuslabs.lingua.franca.annotations.processors.AnnotationProcessor;

import java.io.File;
import java.io.IOException;

import java.util.*;
import javax.lang.model.element.*;

import static javax.tools.Diagnostic.Kind.*;


public abstract class TemplateProcessor extends AnnotationProcessor {

    protected static final List<Locale> LOCALES = List.of(Locale.getAvailableLocales());
    
    
    protected boolean processLocales(String annotation, String[] locales, Element element) {
        if (locales.length == 0) {
            messager.printMessage(ERROR, "Invalid locales for @" + annotation + " annotated type, " + element.asType().toString() + ", locales cannot be empty", element);
            return false;
            
        } else {
            for (var locale : locales) {
                if (!LOCALES.contains(Locales.of(locale))) {
                    messager.printMessage(WARNING, "Unknown locale, " + locale + " for @" + annotation + " annotated type, " + element.asType().toString(), element);
                }
            }
            
            return true;
        }
    }

    protected boolean processEmbedded(String annotation, String type, String template, Element element) {
        var file = new File(RESOURCES, template);
        var valid = !template.isEmpty() && file.exists() && file.isFile();
        if (!valid) {
            messager.printMessage(ERROR, "Invalid " + type + " specified for @" + annotation + " annotated type, " + element.asType().toString() + ", " + template + " must exist and be a file", element);
        }
        
        return valid;
    }

    
}
