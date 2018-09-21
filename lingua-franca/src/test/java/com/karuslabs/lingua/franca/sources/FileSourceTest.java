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

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.of;


@ExtendWith(MockitoExtension.class)
class FileSourceTest {
    
    static FileSource source = new SystemSource("folder");
    
    
    @ParameterizedTest
    @MethodSource({"equality_provider"})
    void equals(FileSource other, boolean expected) {
        assertEquals(expected, source.equals(other));
    }
    
    
    @ParameterizedTest
    @MethodSource({"equality_provider"})
    void hashCode(FileSource other, boolean expected) {
        assertEquals(expected, source.hashCode() == other.hashCode());
    }
    
    
    static Stream<Arguments> equality_provider() {
        return Stream.of(
            of(source, true),
            of(new SystemSource("folder"), true),
            of(new SystemSource("folder/"), true),
            of(new SystemSource("foLder"), false),
            of(new ClassLoaderSource("folder"), false)
        );
    }
    
    
    @ParameterizedTest
    @MethodSource({"toString_provider"})
    void source_toString(String folder, String expected) {
        assertEquals(expected, new SystemSource(folder).toString());
    }
    
    static Stream<Arguments> toString_provider() {
        var name = SystemSource.class.getName() + "[folder = %s]";
        return Stream.of(
            of("", String.format(name, "")),
            of("folder", String.format(name, "folder/")),
            of("folder/", String.format(name, "folder/"))
        );
    }
    
}
