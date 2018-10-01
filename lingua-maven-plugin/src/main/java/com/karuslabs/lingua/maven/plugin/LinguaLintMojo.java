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

import com.karuslabs.lingua.franca.annotations.ClassLoaderSources;

import io.github.classgraph.ClassGraph;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.*;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_SOURCES;


@Mojo(name = "lingua-lint", defaultPhase = PROCESS_SOURCES, threadSafe = false)
public class LinguaLintMojo extends AbstractMojo {
    
    @Parameter(defaultValue = "${project.basedir}/src/main/resources")
    protected File resources;
    
    @Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
    protected List<String> elements;
    
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        URL[] urls = elements.stream().map(element -> {
            try {
                return new File(element).toURI().toURL();

            } catch (MalformedURLException e) {
                throw new UncheckedIOException(e);
            }
        }).toArray(URL[]::new);

        var results = new ClassGraph().enableAllInfo().overrideClassLoaders(URLClassLoader.newInstance(urls, getClass().getClassLoader())).whitelistPackages("*").scan();
        
        getLog().error(results.getClasspath());
        
        for (var info : results.getAllAnnotations()) {
            getLog().error("Annotation: " + info.getClass());
        }
    }
    
}
