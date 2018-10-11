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
package com.karuslabs.lingua.maven.plugin.lint.processors;

import com.karuslabs.lingua.franca.template.annotations.*;
import com.karuslabs.lingua.maven.plugin.TemplateProcessor;

import java.io.File;
import java.util.Collection;

import org.apache.maven.plugin.logging.Log;


/**
 * Determines if a {@link com.karuslabs.lingua.franca.template.annotations.Platform Platform} annotation
 * contains a valid template and non-empty locales.
 */
public class PlatformProcessor extends TemplateProcessor {
    
    /**
     * Creates a {@code PlatformProcessor}
     * 
     * @param resources 
     */
    public PlatformProcessor(File resources) {
        super(resources);
    }
    
    
    /**
     * Determines if the annotated classes contain valid templates and non=empty locales. 
     * 
     * @param classes the annotated classes
     * @param logger the logger
     * @return true if the annotated classes contain valid template and non-empty locales
     */
    @Override
    public boolean process(Collection<Class<?>> classes, Log logger) {
        boolean success = true;
        
        for (var type : classes)  {
            for (var annotation : type.getAnnotationsByType(Platform.class)) {
                success &= processPlatform(logger, type, annotation.template());
                success &= processLocales(logger, type, "Platform", annotation.locales());
            }
        }
        
        return success;
    }
    
    /**
     * Determines if the nested {@link com.karuslabs.lingua.franca.template.annotations.In In} annotation
     * contains either a valid embedded or system template, emitting a error if none or both is specified.
     * 
     * @param logger the logger
     * @param type the annotated class
     * @param annotation the annotated
     * @return true if the annotation contains a single template
     */
    protected boolean processPlatform(Log logger, Class<?> type, In annotation) {
        if (annotation.embedded().isEmpty() && annotation.system().isEmpty()) {
            logger.error("Invalid @Plaform annotation for " + type.getName() + ", @" + annotation + " must contain either an embeded or system template");
            return false;
                
        } else if (!annotation.embedded().isEmpty() && !annotation.system().isEmpty()) {
            logger.error("Invalid @Platform annotated for " + type.getName() + ", @" + annotation + " must contain only a single template");
            return false;
            
        } else {
            return processEmbedded(logger, type, "Plaform", annotation.embedded());
        }       
    }
    
}
