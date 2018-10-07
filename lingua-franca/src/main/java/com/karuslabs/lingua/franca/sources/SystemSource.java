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
 * A {@code Source} implementation from which file resources on the system can be 
 * retrieved during runtime.
 */
public class SystemSource extends FileSource {
    
    /**
     * The source for the directory relative to the application during runtime.
     */
    public static final SystemSource RELATIVE_ROOT = new SystemSource("./");
    
    
    /**
     * Creates a {@code SystemSource} with the specified folder.
     * 
     * @param folder the folder
     */
    public SystemSource(String folder) {
        super(folder);
    }

    
    /**
     * Creates a stream for the specified resource in the system during runtime.
     * 
     * @param resource the resource
     * @return a stream for the specified resource, or null if a stream could not be created
     */
    @Override
    public @Nullable InputStream load(String resource) {
        var file = new File(folder, resource);
        if (!file.isFile() || !file.canRead()) {
            return null;
        }
        
        try {
            return new BufferedInputStream(new FileInputStream(file));
            
        } catch (FileNotFoundException ignored) {
            return null;
        }
    }
    
}
