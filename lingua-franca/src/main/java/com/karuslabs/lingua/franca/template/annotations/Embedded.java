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
package com.karuslabs.lingua.franca.template.annotations;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Indicates the generation of locale files from a template to be embedded in the 
 * JAR at compile-time.
 * <p>
 * This annotation is to be used in conjunction with a build tool plugin to detect
 * and generate the locale files during compilation. Generation of the locale files
 * using Maven is supported via the Lingua Maven Plugin using the {@code lingua-maven-plugin:generate} goal.
 */
@Documented
@Retention(RUNTIME)
@Repeatable(Embeddings.class)
@Target(TYPE)
public @interface Embedded {
    
    /**
     * The template file.
     * 
     * @return the template file
     */
    String template();
    
    /**
     * The destination folder in which the locales files are to be embedded.
     * 
     * @return the destination folder
     */
    String destination();
    
    /**
     * The locales for which locale files are generated.
     * 
     * @return the locales
     */
    String[] locales();
    
}
