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
package com.karuslabs.lingua.franca.sources;

import java.io.*;

import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * A {@code Source} implementation from which file resources relative to a module
 * can be retrieved using the module. This class can be used to retrieve 
 * embedded resources.
 */
public class ModuleSource extends FileSource {
    
    /**
     * The source for the root folder relative to the {@code Module}.
     */
    public static final ModuleSource ROOT = new ModuleSource("");
    
    
    private final Module module;
    
    
    /**
     * Creates a {@code ModuleSource} with the specified folder using the module
     * of the calling class.
     * <p>
     * This method is caller sensitive.
     * 
     * @param folder the folder
     */
    public ModuleSource(String folder) {
        this(STACK.getCallerClass().getModule(), folder);
    }
    
    /**
     * Creates a {@code ModuleSource} with the specified module and folder.
     * 
     * @param module the module
     * @param folder the folder
     */
    public ModuleSource(Module module, String folder) {
        super(folder);
        this.module = module;
    }

    
    /**
     * Creates a stream for the specified resource relative to the module.
     * 
     * @param resource the resource
     * @return a stream for the specified resource, or null if a stream could not be created
     */
    @Override
    public @Nullable InputStream load(String resource) {
        try {
            return module.getResourceAsStream(folder + resource);
            
        } catch (IOException ignored) {
            return null;
        }
    }
    
    
    @Override
    public boolean equals(Object other) {
        return super.equals(other) && module.equals(((ModuleSource) other).module);
    }
    
    @Override
    public int hashCode() {
        return 53 * super.hashCode() + module.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("%s[module = %s, folder = %s]", getClass().getName(), module, folder);
    }
    
}
