
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
package com.karuslabs.lingua.maven.plugin.lint;

import com.karuslabs.lingua.franca.annotations.*;
import com.karuslabs.lingua.franca.spi.annotations.Provides;
import com.karuslabs.lingua.franca.template.annotations.*;

import com.karuslabs.lingua.maven.plugin.LinguaMojo;
import com.karuslabs.lingua.maven.plugin.lint.processors.*;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.Mojo;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;


@Mojo(name = "lint", defaultPhase = PROCESS_CLASSES, threadSafe = false)
public class LinguaLintMojo extends LinguaMojo {
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Compile classpaths for project detected - analyzing project");
        var reflection = reflection();
        var success = true;
        
        success &= new NamespaceProcessor().process(reflection.getTypesAnnotatedWith(Namespace.class), getLog());
        success &= new EmbeddedProcessor(resources).process(reflection.getTypesAnnotatedWith(Embedded.class), getLog());
        success &= new PlatformProcessor(resources).process(reflection.getTypesAnnotatedWith(Platform.class), getLog());
        success &= new ProvidesProcessor().process(reflection.getTypesAnnotatedWith(Provides.class), getLog());

        var sources = reflection.getTypesAnnotatedWith(ClassLoaderSources.class);
        sources.addAll(reflection.getTypesAnnotatedWith(ModuleSources.class));

        success &= new SourcesProcessor(resources).process(sources, getLog());

        if (success) {
            getLog().info("Analysis completed successfully");

        } else {
            throw new MojoFailureException("Analysis completed - detected potential issues");
        }
    }
    
}
