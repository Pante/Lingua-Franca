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


public class ModuleSource extends FileSource {
    
    public static final ModuleSource ROOT = new ModuleSource("");
    
    
    private final Module module;
    
    
    public ModuleSource(String folder) {
        this(STACK.getCallerClass().getModule(), folder);
    }
    
    public ModuleSource(Module module, String folder) {
        super(folder);
        this.module = module;
    }

    
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
        return String.format(getClass().getName() + "[module = %s, folder = %s]", module.toString(), folder);
    }
    
}
