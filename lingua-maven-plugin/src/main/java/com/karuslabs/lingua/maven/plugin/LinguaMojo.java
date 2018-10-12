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

import java.io.File;
import java.net.*;
import java.util.List;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.Parameter;

import org.reflections.Reflections;


/**
 * This class provides a skeletal implementation of a Lingua Maven Plugin Mojo.
 */
public abstract class LinguaMojo extends AbstractMojo {
    
    /**
     * The compile classpaths.
     */
    @Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
    public List<String> elements;
    
    /**
     * The folder in which embedded resources are located.
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/resources")
    public File resources;
    
    
    /**
     * Creates a {@code Reflections} instance using the classpaths specified by {@link #elements}.
     * 
     * @return a Reflections instance
     * @throws MojoExecutionException if a malformed classpath was specified
     */
    public Reflections reflection() throws MojoExecutionException {
        var urls = new URL[elements.size()];
        try {
            for (int i = 0; i < elements.size(); i++) {
                urls[i] = new File(elements.get(i)).toURI().toURL();
            }
            return new Reflections(new URLClassLoader(urls, getClass().getClassLoader()));
            
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Failed to load compile classpaths", e);
        }
    }
    
}
