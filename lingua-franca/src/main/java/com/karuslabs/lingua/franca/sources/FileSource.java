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

import java.lang.StackWalker.Option;


public abstract class FileSource implements Source {
    
    static final StackWalker STACK = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
    
    protected final String folder;
    private volatile int hash;
    
    
    protected FileSource(String folder) {
        this.folder = folder.isEmpty() || folder.charAt(folder.length() - 1) == '/' ? folder : folder + "/";
        this.hash = 0;
    }
    
    
    @Override
    public boolean equals(Object other) {
        return this == other || other != null && getClass() == other.getClass()
                && folder.equals(((FileSource) other).folder);
    }

    @Override
    public int hashCode() {
        int value = hash;
        if (value == 0) {
            value = 5;
            value = 53 * value + getClass().hashCode();
            value = 53 * value + folder.hashCode();
            hash = value;
        }
        
        return value;
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "[folder = " + folder + "]";
    }
    
}
