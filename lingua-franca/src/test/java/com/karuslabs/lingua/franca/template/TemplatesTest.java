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
package com.karuslabs.lingua.franca.template;

import com.karuslabs.lingua.franca.codec.Stringifier;
import com.karuslabs.lingua.franca.template.annotations.*;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TemplatesTest {
    
    static final String LOADED = "loaded.yml";
    static final String DESTINATION = "./target/test-classes/";
    static final List<Locale> LOCALES = List.of(Locale.JAPAN, Locale.GERMANY);
    static final File GERMANY = new File(DESTINATION, "loaded_de_DE.yml");
    static final File JAPAN = new File(DESTINATION, "loaded_ja_JP.yml");
    static final File SOURCE;
    
    static {
        try {
            SOURCE = new File(TemplatesTest.class.getClassLoader().getResource(LOADED).toURI());
            
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    @Embedded(template = LOADED, locales = {"de_DE", "ja_JP"}, destination = DESTINATION)
    @Platform(template = @In(embedded = LOADED), locales = "de_DE", destination = DESTINATION)
    @Platform(template = @In(system = DESTINATION + "loaded.yml"), locales = "ja_JP", destination = DESTINATION)
    static class Success {
        
    }
    
    
    @Embedded(template = LOADED, locales = {"de_DE", "en_GB", "ja_JP"}, destination = DESTINATION)
    @Platform(template = @In(embedded = LOADED), locales = {"de_DE", "en_GB", "ja_JP"}, destination = DESTINATION)
    static class Failure {
        
    }
    
    
    @Platform(template = @In, locales = {"de_DE", "ja_JP"}, destination = DESTINATION)
    static class Invalid {
        
    }
    
            
    @AfterEach
    void clear() throws IOException {
        GERMANY.delete();
        JAPAN.delete();
    }
    
    
    @Test
    void fromPlatforms_object() {
        assertTrue(Templates.fromPlatforms(new Success()));
        
        assertTrue(GERMANY.exists());
        assertTrue(JAPAN.exists());
    }
    
    
    @Test
    void fromPlatforms_class() {
        assertTrue(Templates.fromPlatforms(Success.class));
        
        assertTrue(GERMANY.exists());
        assertTrue(JAPAN.exists());
    }
    
    
    @Test
    void fromPlatforms_failure() {
        assertFalse(Templates.fromPlatforms(Failure.class));
        
        assertTrue(GERMANY.exists());
        assertTrue(JAPAN.exists());
    }
    
    
    @Test
    void fromPlatform_exception() {
        assertEquals(
            "Invalid template, either an embedded or system template must be specified", 
            assertThrows(IllegalArgumentException.class, () -> Templates.fromPlatforms(Invalid.class)).getMessage()
        );
    }
    
    
    @Test
    void from_classloader_success() {
        assertTrue(Templates.fromClassLoader(LOADED, LOCALES, DESTINATION));
        
        assertTrue(GERMANY.exists());
        assertTrue(JAPAN.exists());
    }
    
    
    @Test
    void from_module_success() {
        assertTrue(Templates.fromModule(LOADED, LOCALES, DESTINATION));
        
        assertTrue(GERMANY.exists());
        assertTrue(JAPAN.exists());
    }
    
    
    @Test
    void from_module_exception() throws IOException {
        Module module = when(mock(Module.class).getResourceAsStream(any())).thenThrow(IOException.class).getMock();
        assertThrows(UncheckedIOException.class, () -> Templates.fromModule(LOADED, module, LOCALES, DESTINATION));
    }
    
    
    @Test
    void from_file_success() {
        var success = Templates.from(SOURCE, LOCALES, DESTINATION);
        assertTrue(success);
        
        assertTrue(GERMANY.exists());
        assertTrue(JAPAN.exists());
    }
    
    
    @Test
    void from_file_exception() {
        assertThrows(UncheckedIOException.class, () -> Templates.from(new File("I do not exist"), LOCALES, DESTINATION));
    }
    
    
    @Test
    void from_file_stream_success() {
        var success = Templates.from(SOURCE, getClass().getClassLoader().getResourceAsStream(LOADED), LOCALES, DESTINATION);
        assertTrue(success);
        
        assertTrue(GERMANY.exists());
        assertTrue(JAPAN.exists());
    }
    
    
    @Test
    void from_file_stream_exception() {
        Executable function = () -> Templates.from(new File("hurhur"), getClass().getClassLoader().getResourceAsStream(LOADED), LOCALES, DESTINATION);
        assertEquals("Invalid file name, file name is either blank or missing an extension", assertThrows(IllegalArgumentException.class, function).getMessage());
    }
            
    
    @Test
    void from_name_success() throws FileNotFoundException {
        var success = Templates.from("loaded", "yml", getClass().getClassLoader().getResourceAsStream(LOADED), LOCALES, DESTINATION);
        assertTrue(success);
        
        assertTrue(GERMANY.exists());
        assertTrue(JAPAN.exists());
        
        assertEquals("Hello", Stringifier.stringify().from(new FileInputStream(GERMANY), "yml").get("hello"));
        assertEquals("Hello", Stringifier.stringify().from(new FileInputStream(JAPAN), "yml").get("hello"));
    }
    
    
    @Test
    void from_name_failure() {
        var success = Templates.from("loaded", "yml", getClass().getClassLoader().getResourceAsStream(LOADED), List.of(Locale.JAPAN, Locale.UK, Locale.GERMANY), DESTINATION);
        assertFalse(success);
        
        assertTrue(GERMANY.exists());
        assertTrue(JAPAN.exists());
    }
    
    
    @Test
    void from_name_exception() throws IOException {
        InputStream stream = mock(InputStream.class);
        doThrow(IOException.class).when(stream).close();
        
        assertThrows(UncheckedIOException.class, () -> Templates.from("loaded", "yml", stream, List.of(Locale.UK), DESTINATION));
    }
    
}
