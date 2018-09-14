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
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;
import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;


public class Stringifier extends Visitor<Map<String, Object>, Map<String, Object>> {
    
    private static final Stringifier STRINGIFIER = new Stringifier();
    
    
    public static Stringifier stringify() {
        return STRINGIFIER;
    }
    
    
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final ObjectMapper PROPERTIES = new JavaPropsMapper();
    private static final ObjectMapper XML = new XmlMapper();
    private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());
    
    private static final String[] EMPTY = new String[0];

    
    protected Stringifier() {
        super(null);
    }


    public @Nullable Map<String, Object> from(InputStream stream, String format) {
        try (stream) {
            return visit("", mapper(format).readTree(stream), new HashMap<>());

        } catch (IOException e) {
            return null;
        }
    }
    
    protected ObjectMapper mapper(String format) {
        switch (format) {
            case "json":
                return JSON;

            case "properties":
                return PROPERTIES;
                            
            case "xml":
                return XML;
                
            case "yml": case "yaml":
                return YAML;

            default:
                throw new UnsupportedOperationException("Unsupported format: " + format);
        }
    }
        
    @Override
    protected Map<String, Object> visit(String path, ObjectNode node, Map<String, Object> map) {
        var prefix = path.isEmpty() ? "" : path + ".";
        var fields = node.fields();
        while (fields.hasNext()) {
            var entry = fields.next();
            visit(prefix + entry.getKey(), entry.getValue(), map);
        }
        
        return map;
    }
    
    @Override
    protected Map<String, Object> visit(String path, ArrayNode array, Map<String, Object> map) {
        if (array.size() == 0) {
            map.put(path, EMPTY);
            return map;
        }

        var strings = new ArrayList<String>(array.size());
        for (int i = 0; i < array.size(); i++) {
            var value = array.get(i);
            if (value.isArray() || value.isObject()) {
                visit(path + "[" + i + "]", value, map);

            } else {
                strings.add(value.asText());
            }
        }

        if (!strings.isEmpty()) {
            map.put(path, strings.toArray(EMPTY));
        }
        
        return map;
    }

    @Override
    protected Map<String, Object> visit(String path, ValueNode value, Map<String, Object> map) {
        map.put(path, value.asText());
        return map;
    }
    
}
