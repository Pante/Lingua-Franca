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

import java.util.ResourceBundle.Control;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class BundleLoaderTest {
    
    static final BundleLoader CACHED = BundleLoader.loader();
    static final Source named1 = new ClassLoaderSource("named1");
    static final Source named2 = new ClassLoaderSource("named2");
    static final Source unnamed1 = new ClassLoaderSource("unnamed3");
    static final Source unnamed2 = new ClassLoaderSource("unnamed4");
    static final Control CONTROL = Control.getControl(Control.FORMAT_DEFAULT);

    static {
        CACHED.add("named", named1, named2);
        CACHED.add(unnamed1, unnamed2);
    }
    
    
    @Namespace("named")
    @ClassLoaderSources("a")
    @ModuleSources("b")
    @SystemSources("c")
    static class AnnotatedNamespace {
        
    }
    
    
    @ClassLoaderSources("a")
    @ModuleSources("b")
    @SystemSources("c")
    static class AnnotatedGlobal {
        
    }
    
    
    final BundleLoader loader = new BundleLoader();
    
    
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
        assertEquals(CONTROL.getCandidateLocales("", Locale.SIMPLIFIED_CHINESE), loader.parents("", Locale.SIMPLIFIED_CHINESE));
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
    void add_namespaces_array() {
        assertTrue(loader.add("named", named1, named2));
        assertTrue(loader.namespaces.get("named").equals(Set.of(named1, named2)));
        
        assertTrue(loader.add("named", unnamed1, unnamed2));
        assertTrue(loader.namespaces.get("named").equals(Set.of(named1, named2, unnamed1, unnamed2)));
    }
    
    
    @Test
    void add_namespaces_collection() {
        assertTrue(loader.add("named", List.of(named1, named2)));
        assertTrue(loader.namespaces.get("named").equals(Set.of(named1, named2)));
        
        assertTrue(loader.add("named", List.of(unnamed1, unnamed2)));
        assertTrue(loader.namespaces.get("named").equals(Set.of(named1, named2, unnamed1, unnamed2)));
    }
    
    
    @Test
    void add_global() {
        assertFalse(CACHED.add(unnamed1));
        assertTrue(loader.add(unnamed1));
    }
    
    
    @Test     
    void add_globals_array() {
        assertFalse(CACHED.add(unnamed1, unnamed2));
        
        loader.add(unnamed1);
        assertTrue(loader.add(unnamed1, unnamed2));
    }
    
    
    @Test
    void add_globals_collection() {
        assertFalse(CACHED.add(List.of(unnamed1, unnamed2)));
        
        loader.add(unnamed1);
        assertTrue(loader.add(List.of(unnamed1, unnamed2)));
    }
    
    
    @Test
    void contains_name() {
        assertFalse(CACHED.contains("nAmed"));
        assertTrue(CACHED.contains("named"));
    }
    
    
    @Test
    void contains_namespace() {
        assertFalse(CACHED.contains("named", unnamed1));
        assertTrue(CACHED.contains("named", named1));
    }
    
    
    @Test
    void contains_namespaces() {
        assertFalse(CACHED.contains("named", named1, unnamed1));
        assertTrue(CACHED.contains("named", named1, named2));
    }
    
    
    @Test
    void contains_global() {
        assertFalse(CACHED.contains(named1));
        assertTrue(CACHED.contains(unnamed1));
    }
        
    
    @Test
    void contains_globals() {
        assertFalse(CACHED.contains(named1, unnamed1));
        assertTrue(CACHED.contains(unnamed1, unnamed2));
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
