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

import com.karuslabs.lingua.franca.annotations.*;
import com.karuslabs.lingua.franca.sources.*;

import java.io.*;
import java.util.ResourceBundle.Control;
import java.util.*;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BundleLoaderTest {
    
    static BundleLoader cached = BundleLoader.loader();
    static Source named1 = new ClassLoaderSource("named1");
    static Source named2 = new ClassLoaderSource("named2");
    static Source unnamed1 = new ClassLoaderSource("unnamed3");
    static Source unnamed2 = new ClassLoaderSource("unnamed4");
    static Control control = Control.getControl(Control.FORMAT_DEFAULT);
    
    static {
        cached.add("named", named1, named2);
        cached.add(unnamed1, unnamed2);
    }
    
    
    @Bundled("named")
    @ClassLoaderSources({"a"})
    @ModuleSources({"b"})
    @SystemSources({"c"})
    static class AnnotatedNamespace {
        
    }
    
    
    @ClassLoaderSources({"a"})
    @ModuleSources({"b"})
    @SystemSources({"c"})
    static class AnnotatedGlobal {
        
    }
    
    
    BundleLoader loader = new BundleLoader();
    
    
    @Test
    void load() {
        loader.add("loaded", new ClassLoaderSource("sources"));
        loader.add(new ClassLoaderSource(""));
        
        var bundle = loader.load("loaded", Locale.UK, Bundle.EMPTY);
        
        assertEquals("Morning", bundle.find("hello"));
        assertEquals(Bundle.EMPTY, bundle.parent());
    }
    
    
    @Test
    void parents() {
        assertEquals(control.getCandidateLocales("", Locale.SIMPLIFIED_CHINESE), loader.parents("", Locale.SIMPLIFIED_CHINESE));
    }
    
    
    @Test
    void toResourceName() {
        assertEquals(control.toResourceName("bundled", "yml"), loader.toResourceName("bundled", "yml"));
    }
    
    
    @Test
    void toBundleName() {
        assertEquals(control.toBundleName("bundled", Locale.ENGLISH), loader.toBundleName("bundled", Locale.ENGLISH));
    }
    
    
    @Test
    void add_annotated_namespace() {
        loader.add(new AnnotatedNamespace());
        assertEquals(Set.of(new ClassLoaderSource("a"), new ModuleSource("b"), new SystemSource("c")), loader.namespaces.get("named"));
    }
    
    
    @Test
    void add_annotated_global() {
        loader.add(new AnnotatedGlobal());
        assertEquals(Set.of(new ClassLoaderSource("a"), new ModuleSource("b"), new SystemSource("c")), loader.global);
    }
    
    
    @Test
    void add_namespace() {
        assertTrue(loader.add("named", named1));
        assertTrue(loader.namespaces.get("named").equals(Set.of(named1)));
        
        assertTrue(loader.add("named", named2));
        assertTrue(loader.namespaces.get("named").equals(Set.of(named1, named2)));
    }
    
    
    @Test
    void add_namespaces() {
        assertTrue(loader.add("named", named1, named2));
        assertTrue(loader.namespaces.get("named").equals(Set.of(named1, named2)));
        
        assertTrue(loader.add("named", unnamed1, unnamed2));
        assertTrue(loader.namespaces.get("named").equals(Set.of(named1, named2, unnamed1, unnamed2)));
    }
    
    
    @Test
    void add_global() {
        assertFalse(cached.add(unnamed1));
        assertTrue(loader.add(unnamed1));
    }
    
    
    @Test     
    void add_globals() {
        assertFalse(cached.add(unnamed1, unnamed2));
        
        loader.add(unnamed1);
        assertTrue(loader.add(unnamed1, unnamed2));
    }
    
    
    @Test
    void contains_name() {
        assertFalse(cached.contains("nAmed"));
        assertTrue(cached.contains("named"));
    }
    
    
    @Test
    void contains_namespace() {
        assertFalse(cached.contains("named", unnamed1));
        assertTrue(cached.contains("named", named1));
    }
    
    
    @Test
    void contains_namespaces() {
        assertFalse(cached.contains("named", named1, unnamed1));
        assertTrue(cached.contains("named", named1, named2));
    }
    
    
    @Test
    void contains_global() {
        assertFalse(cached.contains(named1));
        assertTrue(cached.contains(unnamed1));
    }
        
    
    @Test
    void contains_globals() {
        assertFalse(cached.contains(named1, unnamed1));
        assertTrue(cached.contains(unnamed1, unnamed2));
    }
    
    
    @Test
    void remove_name() {
        loader.add("named", named1);
        assertNull(loader.remove("unnamed"));
        assertTrue(loader.remove("named").contains(named1));
    }
    
    
    @Test
    void remove_namespace() {
        loader.add("named", named1);
        assertFalse(loader.remove("named", unnamed1));
        assertTrue(loader.remove("named", named1));
    }
    
    
    @Test
    void remove_namespaces() {
        loader.add("named", named1, named2);
        assertFalse(loader.remove("named", unnamed1, unnamed2));
        assertTrue(loader.remove("named", named1, unnamed2));
    }
    
    
    @Test
    void remove_global() {
        loader.add(unnamed1);
        assertFalse(loader.remove(named1));
        assertTrue(loader.remove(unnamed1));
    }
    
    
    @Test
    void remove_globals() {
        loader.add(unnamed1, unnamed2);
        assertFalse(loader.remove(named1, named2));
        assertTrue(loader.remove(unnamed1, unnamed2));
    }
    
}
