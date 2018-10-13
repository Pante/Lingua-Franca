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

import com.karuslabs.lingua.franca.annotations.Namespace;

import com.karuslabs.lingua.maven.plugin.Processor;

import java.util.Collection;

import org.apache.maven.plugin.logging.Log;


public class NamespaceProcessor implements Processor {
    
    /**
     * Determines if the annotated classes contain non-empty bundle names.
     * 
     * @param classes the annotated classes
     * @param logger the logger
     * @return true if all annotated classes contain a non-empty bundle name
     */
    @Override
    public boolean process(Collection<Class<?>> classes, Log logger) {
        var success = true;
        
        for (var type : classes) {
            var annotation = type.getAnnotation(Namespace.class);
            if (annotation.value().isEmpty()) {
                logger.error("Invalid @Namespace annotation for " + type.getName() + ", @Namespace cannot be empty");
                success = false;
            }
        }
        
        return success;
    }
    
}
