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
package com.karuslabs.lingua.franca;

import java.util.*;


/**
 * This class consists exclusively of static assertions for the contents of a given bundle.
 */
public class Assert {
    
    /**
     * Determines if the entries in the specified bundle and its parents are a subset
     * of those in the root bundle.
     * 
     * @param bundle the bundle to be asserted
     * @return true if the root bundle contains all entries in the specified bundle and its parents
     */
    public static boolean subset(Bundle bundle) {
        var root = bundle;
        while (root.parent() != Bundle.EMPTY) {
            root = root.parent();
        }
        
        while (bundle != Bundle.EMPTY) {
            if (!subset(root.messages, bundle.messages)) {
                return false;
            }
            bundle = bundle.parent();
        }
        
        return true;
    }
    
    /**
     * Determines if the entries in the specified child are a subset of those in the
     * specified parent map.
     * <p>
     * This method expects the values in the specified parent and child to be either
     * strings or arrays of strings. If a value of the child is an array and the
     * array length is less than or equal to that of the parent, the child entry 
     * is considered a subset.
     * 
     * @param parent the parent map which contains string and array of strings
     * @param child the child map which contains string and array of strings
     * @return true if the parent map contains all entries in the child map
     */
    public static boolean subset(Map<String, Object> parent, Map<String, Object> child) {
        for (var entry : child.entrySet()) {
            if (!subset(parent.get(entry.getKey()), entry.getValue())) {
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean subset(Object parent, Object child) {
        if (parent instanceof String[] && child instanceof String[]) {
            return ((String[]) child).length <= ((String[]) parent).length;
            
        } else {
            return parent instanceof String && child instanceof String;
        }
    }
    
}
