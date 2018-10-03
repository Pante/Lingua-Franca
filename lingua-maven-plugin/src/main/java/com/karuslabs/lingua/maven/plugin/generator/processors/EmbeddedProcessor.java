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

import com.karuslabs.lingua.franca.Locales;
import com.karuslabs.lingua.franca.template.Templates;
import com.karuslabs.lingua.franca.template.annotations.Embedded;
import com.karuslabs.lingua.maven.plugin.TemplateProcessor;

import java.io.*;
import java.util.*;

import org.apache.maven.plugin.logging.Log;


public class EmbeddedProcessor extends TemplateProcessor {

    public EmbeddedProcessor(File resources) {
        super(resources);
    }
    
    
    @Override
    public boolean process(Collection<Class<?>> classes, Log logger) {
        var success = true;
        for (var type : classes) {
            for (var annotation : type.getAnnotationsByType(Embedded.class)) {
                success &= generate(logger, type, annotation);
            }
        }
        
        return success;
    }
    
    protected boolean generate(Log logger, Class<?> type, Embedded annotation) {
        try {
            var locales = new ArrayList<Locale>(annotation.locales().length);
            for (var locale : annotation.locales()) {
                locales.add(Locales.of(locale));
            }
            if (!Templates.fromClassLoader(annotation.template(), getClass().getClassLoader(), locales, new File(resources, annotation.destination()).getPath())) {
                logger.info("Files already exist for template, " + annotation.template() + " in @Embedded annotation for " + type.getName());
            }
            return true;

        } catch (IllegalArgumentException | UncheckedIOException e) {
            logger.error("Exception occured while generating file(s) for template: " + annotation.template()
                    + " in @Embeded annotation for " + type.getName() + ": " + e.getMessage());
            return false;
        }
    }

}
