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
package com.karuslabs.lingua.maven.plugin.generator;

import com.karuslabs.lingua.franca.template.annotations.Embedded;
import com.karuslabs.lingua.maven.plugin.LinguaMojo;
import com.karuslabs.lingua.maven.plugin.generator.processors.EmbeddedProcessor;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.Mojo;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;


/**
 * This Mojo generates default locale files to be embedded in the resultant JAR 
 * from a template in the resources folder at compile-time.
 */
@Mojo(name = "generate", defaultPhase = PROCESS_CLASSES, threadSafe = false)
public class LinguaGenerateMojo extends LinguaMojo {
    
    /**
     * Scans the classpath(s) for classes annotated with {@link com.karuslabs.lingua.franca.template.annotations.Embedded Embedded} annotations,
     * and generates the default locale files from the template specified by the annotation.
     * 
     * @throws MojoExecutionException if an unexpected error occurs during generation
     * @throws MojoFailureException if an expected error occurs during generation
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var reflection = reflection();
        
        getLog().info("Compile classpaths for project detected - generating locale files");
        
        if (processor().process(reflection.getTypesAnnotatedWith(Embedded.class), getLog())) {
            getLog().info("Generation completed successfully");
            
        } else {
            throw new MojoFailureException("Generation completed - failed to generate files.");
        }
    }
    
    /**
     * Creates an {@code EmbeddedProcessor}.
     * 
     * @return an EmbeddedProcessor
     */
    protected EmbeddedProcessor processor() {
        return new EmbeddedProcessor(resources);
    }
    
}
