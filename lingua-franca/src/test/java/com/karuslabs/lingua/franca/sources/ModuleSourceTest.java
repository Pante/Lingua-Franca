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

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ModuleSourceTest {
    
    static final ModuleSource SOURCE = new ModuleSource("sources");
    static final String FILE = "source.yml";
    
    
    @Test
    void load() {
        assertNotNull(new ModuleSource("sources").load(FILE));
    }
    
    @Test
    void load_exception() throws IOException {
        Module module = when(mock(Module.class).getResourceAsStream(any())).thenThrow(IOException.class).getMock();
        assertNull(new ModuleSource(module, "sources").load(FILE));
    }
    
    
    @ParameterizedTest
    @MethodSource("equality_provider")
    void equals(FileSource other, boolean expected) {
        assertEquals(expected, SOURCE.equals(other));
    }
    
    
    @ParameterizedTest
    @MethodSource("equality_provider")
    void hashCode(FileSource other, boolean expected) {
        assertEquals(expected, SOURCE.hashCode() == other.hashCode());
    }
    
    
    static Stream<Arguments> equality_provider() {
        return Stream.of(
            of(SOURCE, true),
            of(new ModuleSource("sources"), true),
            of(new ModuleSource(mock(Module.class), "sources"), false)
        );
    }
    
    
    @Test
    void source_toString() {
        assertEquals(String.format(ModuleSource.class.getName() + "[module = %s, folder = %s]", ModuleSource.class.getModule(), "sources/"), new ModuleSource("sources").toString());
    }
    
}
