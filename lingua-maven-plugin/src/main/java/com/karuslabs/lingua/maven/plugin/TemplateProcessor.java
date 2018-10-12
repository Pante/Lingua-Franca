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
package com.karuslabs.lingua.maven.plugin;

import com.karuslabs.lingua.franca.Locales;

import java.io.File;
import java.util.*;

import org.apache.maven.plugin.logging.Log;


/**
 * This class provides a skeletal implementation of a processor for annotations which
 * contain locales and embedded templates.
 */
public abstract class TemplateProcessor implements Processor {
    
    /**
     * The available locales.
     */
    protected static final Set<Locale> LOCALES = Set.of(Locale.getAvailableLocales());
    
    /**
     * The resources folder which contains embedded locale files.
     */
    protected File resources;
    
    
    /**
     * Creates a {@code TemplateProcessor} with the specified resources folder.
     * 
     * @param resources the resources folder
     */
    protected TemplateProcessor(File resources) {
        this.resources = resources;
    }
    
    
    /**
     * Compares the specified locales against the available locales, emitting an error
     * and warning if the specified array is empty or not ISO compliant respectively.  
     * 
     * @param logger the logger
     * @param type the annotated class
     * @param annotation the annotation name
     * @param locales the locales
     * @return true if no errors or warning were emitted
     */
    public boolean processLocales(Log logger, Class<?> type, String annotation, String[] locales) {
        if (locales.length == 0) {
            logger.error("Invalid @" + annotation + " annotation for " + type.getName() + ", @" + annotation + " must contain at least one locale");
            return false;
        }
        
        for (var locale : locales) {
            if (!LOCALES.contains(Locales.of(locale))) {
                logger.warn("Unknown locale, " + locale + " found in @" + annotation + " annotation for " + type.getName());
            }
        }
            
        return true;
    }
    
    /**
     * Determines if the specified template can be found relative to the resources folder,
     * emitting an error if the file cannot be found.
     * 
     * @param logger the logger
     * @param type the annotated class
     * @param annotation the annotation name
     * @param template the template
     * @return true if no errors were emitted
     */
    public boolean processEmbedded(Log logger, Class<?> type, String annotation, String template) {
        var file = new File(resources, template);
        var valid = !template.isEmpty() && file.exists() && file.isFile();
        if (!valid) {
            logger.error("Invalid @" + annotation + " annotation for " + type.getName() + ", '" + template + "' either does not exist or is not a file");
        }
        
        return valid;
    }
}
