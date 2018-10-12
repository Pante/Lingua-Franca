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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * A format-independent visitor of trees, in the style of the visitor design pattern.
 * <p>
 * This visitor uses the nodes created by Jackson to traverse the file structure.
 * 
 * @param <T> the type of the additional parameter to the methods of this visitor
 * @param <R> the return type of the methods of this visitor
 */
public abstract class Visitor<T, R> {
    
    /**
     * The default value.
     */
    protected final R value;

    
    /**
     * Creates a {@code Visitor} with the specified default value.
     * 
     * @param value the default value
     */
    Visitor(R value) {
        this.value = value;
    }
    
    
    /**
     * Visits a {@code JsonNode}.
     * The default implementation delegates execution to the other respective methods.
     * 
     * @param path the current path
     * @param node the node to visit
     * @param value an additional parameter
     * @return a result value
     */
    protected R visit(String path, JsonNode node, T value) {
        switch (node.getNodeType()) {
            case ARRAY:
                return visit(path, (ArrayNode) node, value);
                
            case OBJECT:
                return visit(path, (ObjectNode) node, value);
                
            default:
                return visit(path, (ValueNode) node, value);
        }
    }
    
    /**
     * Visits a {@code ValueNode}.
     * The default implementation delegates execution to the other respective methods.
     * 
     * @param path the current path
     * @param node the node to visit
     * @param value an additional parameter
     * @return a result value
     */
    protected R visit(String path, ValueNode node, T value) {
        switch (node.getNodeType()) {
            case BINARY:
                return visit(path, (BinaryNode) node, value);
                
            case BOOLEAN:
                return visit(path, (BooleanNode) node, value);
                
            case MISSING:
                return visit(path, (MissingNode) node, value);
                
            case NULL:
                return visit(path, (NullNode) node, value);
                
            case NUMBER:
                return visit(path, (NumericNode) node, value);
                
            case POJO:
                return visit(path, (POJONode) node, value);
                
            case STRING:
                return visit(path, (TextNode) node, value);
                
            default:
                throw new UnsupportedOperationException("Unsupported node: " + node.getNodeType().name());
        }
    }

    
    /**
     * Visits a {@code ArrayNode}.
     * The default implementation delegates execution to the {@link #visitDefault(String, JsonNode, Object)}.
     * 
     * @param path the current path
     * @param array the array to visit
     * @param value an additional parameter
     * @return a result value
     */
    protected R visit(String path, ArrayNode array, T value) {
        return visitDefault(path, array, value);
    }
    
    /**
     * Visits a {@code ObjectNode}.
     * The default implementation delegates execution to the {@link #visitDefault(String, JsonNode, Object)}.
     * 
     * @param path the current path
     * @param object the object to visit
     * @param value an additional parameter
     * @return a result value
     */
    protected R visit(String path, ObjectNode object, T value) {
        return visitDefault(path, object, value);
    }

    
    /**
     * Visits a {@code BinaryNode}.
     * The default implementation delegates execution to the {@link #visitDefault(String, JsonNode, Object)}.
     * 
     * @param path the current path
     * @param binary the binary to visit
     * @param value an additional parameter
     * @return a result value
     */
    protected R visit(String path, BinaryNode binary, T value) {
        return visitDefault(path, binary, value);
    }
    
    /**
     * Visits a {@code BooleanNode}.
     * The default implementation delegates execution to the {@link #visitDefault(String, JsonNode, Object)}.
     * 
     * @param path the current path
     * @param bool the boolean to visit
     * @param value an additional parameter
     * @return a result value
     */
    protected R visit(String path, BooleanNode bool, T value) {
        return visitDefault(path, bool, value);
    }
    
    /**
     * Visits a {@code MissingNode}.
     * The default implementation delegates execution to the {@link #visitDefault(String, JsonNode, Object)}.
     * 
     * @param path the current path
     * @param missing the missing node to visit
     * @param value an additional parameter
     * @return a result value
     */
    protected R visit(String path, MissingNode missing, T value) {
        return visitDefault(path, missing, value);
    }
    
    /**
     * Visits a {@code NullNode}.
     * The default implementation delegates execution to the {@link #visitDefault(String, JsonNode, Object)}.
     * 
     * @param path the current path
     * @param nil the null node to visit
     * @param value an additional parameter
     * @return a result value
     */
    protected R visit(String path, NullNode nil, T value) {
        return visitDefault(path, nil, value);
    }
    
    /**
     * Visits a {@code NumericNode}.
     * The default implementation delegates execution to the {@link #visitDefault(String, JsonNode, Object)}.
     * 
     * @param path the current path
     * @param number the number to visit
     * @param value an additional parameter
     * @return a result value
     */
    protected R visit(String path, NumericNode number, T value) {
        return visitDefault(path, number, value);
    }
    
    /**
     * Visits a {@code POJONode}.
     * The default implementation delegates execution to the {@link #visitDefault(String, JsonNode, Object)}.
     * 
     * @param path the current path
     * @param pojo the pojo to visit
     * @param value an additional parameter
     * @return a result value
     */
    protected R visit(String path, POJONode pojo, T value) {
        return visitDefault(path, pojo, value);
    }
    
    /**
     * Visits a {@code TextNode}.
     * The default implementation delegates execution to the {@link #visitDefault(String, JsonNode, Object)}.
     * 
     * @param path the current path
     * @param text the text to visit
     * @param value an additional parameter
     * @return a result value
     */
    protected R visit(String path, TextNode text, T value) {
        return visitDefault(path, text, value);
    }
    
    /**
     * The default method to which all visit methods that are not overridden delegates execution.
     * 
     * @param path the current path
     * @param node the node to visit
     * @param value an additional parameter
     * @return the default value
     */
    protected @Nullable R visitDefault(String path, JsonNode node, T value) {
        return this.value;
    }
    
}
