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
package com.karuslabs.example;

import com.karuslabs.lingua.franca.Bundle;
import com.karuslabs.lingua.franca.spi.AnnotatedBundleProvider;
import com.karuslabs.lingua.franca.spi.annotations.Provides;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.checkerframework.checker.nullness.qual.Nullable;


// Used by AnnotatedBundleProvider.provides(..) to check if the provider supports a bundle
@Provides("custom_bundle")
public class CustomBundleProvider extends AnnotatedBundleProvider {

    @Override
    public @Nullable Bundle get(String name, Locale locale) {
        // true if the name is "custom_bundle"
        if (provides(name)) {
            var messages = new ConcurrentHashMap<String, Object>(1);
            messages.put("description", "This is a bundle created by a custom bundle provider, the implementation is actually really straight-forward.");
            
            return new Bundle(messages, locale);
            
        } else {
            return null;
        }
    }
    
}
