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
package com.karuslabs.lingua.franca.codec;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * A {@code Stringifier} recursively flat maps all values from a given file as either 
 * strings or array of strings. 
 * <p>
 * Nested entries are flatten to improve look-up performance from {@code O(n)} to 
 * {@code O(1)}. Keys in subsequent levels are delimited by a full-stop, i.e. {@code path.to.value}. 
 * In addition, strings in arrays can be accessed via enclosing the string index in 
 * square brackets, i.e. {@code path.to.array.value[i]}. Entire arrays can be accessed 
 * similarly to strings, i.e. {@code path.to.array}. Support is provided for properties, 
 * JSON and YAML files.
 */
public class Stringifier extends Visitor<ConcurrentMap<String, Object>, ConcurrentMap<String, Object>> {
    
    private static final Stringifier STRINGIFIER = new Stringifier();
    
    
    /**
     * Returns the global {@code Stringifier}.
     * 
     * @return the global Stringifier
     */
    public static Stringifier stringify() {
        return STRINGIFIER;
    }
    
    
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final ObjectMapper PROPERTIES = new JavaPropsMapper();
    private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());
    
    private static final String[] EMPTY = new String[0];

    
    /**
     * Creates a {@code Stringifier}.
     */
    protected Stringifier() {
        super(null);
    }

    
    /**
     * Flattens and stringifies the entries in the specified stream.
     * 
     * @param stream the stream
     * @param format the format
     * @return the flatten and stringified map, or null if the stream is invalid
     * @throws UnsupportedOperationException if the specified format is not supported
     */
    public @Nullable ConcurrentMap<String, Object> from(InputStream stream, String format) {
        try (stream) {
            return visit("", mapper(format).readTree(stream), new ConcurrentHashMap<>());

        } catch (IOException ignored) {
            return null;
        }
    }
    
    /**
     * Returns a {@code ObjectMapper} for the specified format.
     * 
     * @param format the format
     * @return an appropriate ObjectMapper
     * @throws UnsupportedOperationException if the specified format is not supported
     */
    protected ObjectMapper mapper(String format) {
        switch (format) {
            case "json":
                return JSON;

            case "properties":
                return PROPERTIES;
                
            case "yml": case "yaml":
                return YAML;

            default:
                throw new UnsupportedOperationException("Unsupported format: " + format);
        }
    }
    
    /**
     * Recursively flattens and maps the fields of the specified {@code ObjectNode}
     * to the specified map.
     * 
     * @param path the path to the ObjectNode
     * @param node the object to flatten and map
     * @param map the map to which the values are mapped
     * @return the specified map
     */
    @Override
    protected ConcurrentMap<String, Object> visit(String path, ObjectNode node, ConcurrentMap<String, Object> map) {
        var prefix = path.isEmpty() ? "" : path + ".";
        var fields = node.fields();
        while (fields.hasNext()) {
            var entry = fields.next();
            visit(prefix + entry.getKey(), entry.getValue(), map);
        }
        
        return map;
    }
    
    /**
     * Recursively flattens and maps all values in the specified {@code ArrayNode}
     * to the specified map.
     * 
     * @param path the path to the ArrayNode
     * @param array the array to flatten and map
     * @param map the map to which the values are mapped
     * @return the specified map
     */
    @Override
    protected ConcurrentMap<String, Object> visit(String path, ArrayNode array, ConcurrentMap<String, Object> map) {
        if (array.size() == 0) {
            map.put(path, EMPTY);
            return map;
        }

        var strings = new ArrayList<String>(array.size());
        for (int i = 0; i < array.size(); i++) {
            var value = array.get(i);
            var indexed = path + "[" + i + "]";
            
            if (value.isArray() || value.isObject()) {
                visit(indexed, value, map);

            } else {
                var text = value.asText();
                strings.add(text);
                map.put(indexed, text);
            }
        }

        if (!strings.isEmpty()) {
            map.put(path, strings.toArray(EMPTY));
            
        } else {
            map.put(path, EMPTY);
        }
        
        return map;
    }
    
    /**
     * Converts the specified {@code ValueNode} to a string and adds it to the specified
     * map.
     * 
     * @param path the path to the ArrayNode
     * @param value the value to stringify
     * @param map the map to which the values are mapped
     * @return the specified map
     */
    @Override
    protected ConcurrentMap<String, Object> visit(String path, ValueNode value, ConcurrentMap<String, Object> map) {
        map.put(path, value.asText());
        return map;
    }
    
}
