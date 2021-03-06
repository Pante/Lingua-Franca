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

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import com.karuslabs.lingua.franca.annotations.Namespace;
import com.karuslabs.lingua.franca.sources.ClassLoaderSource;
import com.karuslabs.lingua.franca.spi.BundleProvider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BundlerTest {
    
    static final Bundler CACHED = Bundler.bundler();
    static final String NAME = "loaded";
            
    
    static {
        CACHED.loader().add(ClassLoaderSource.ROOT);
    }
    
    
    final Bundler bundler = new Bundler(CacheBuilder.newBuilder().expireAfterAccess(10, MINUTES).maximumSize(512).build(), BundleLoader.loader());
    final Bundle chained = new Bundle(new ConcurrentHashMap<>(), Locale.UK, new Bundle(new ConcurrentHashMap<>(), Locale.ENGLISH, new Bundle(new ConcurrentHashMap<>(), Locale.ROOT)));
    ServiceLoader<BundleProvider> service;
    
        
    ServiceLoader<BundleProvider> mock_service() {
        BundleProvider provider = when(mock(BundleProvider.class).get("loaded", Locale.UK)).thenReturn(chained).getMock();
        service = (ServiceLoader<BundleProvider>) when(mock(ServiceLoader.class).iterator())
                .thenReturn(List.of(provider).iterator()).getMock();
        return service;
    }
    
    
    @BeforeEach
    void before() {
        Bundler.PROVIDERS.remove();
        bundler.loader().add(ClassLoaderSource.ROOT);
    }
    
    
    @Namespace("loaded")
    static class Annotated {
        
    }
    
    
    @ParameterizedTest
    @MethodSource("reload_provider")
    void reload(Function<Bundler, Bundle> function) {
        bundler.loader().add(ClassLoaderSource.ROOT);
        
        var bundle = function.apply(bundler);
                
        assertEquals(Locale.UK, bundle.locale());
        assertNotSame(bundle, function.apply(bundler));
    }
    
    static Stream<Function<Bundler, Bundle>> reload_provider() {
        return Stream.of(
            bundler -> bundler.reload(new Annotated(), Locale.UK),
            bundler -> bundler.reload(Annotated.class, Locale.UK),
            bundler -> bundler.reload(NAME, Locale.UK)
        );
    }
    
    
    @ParameterizedTest
    @MethodSource("load_provider")
    void load(Function<Bundler, Bundle> function) {
        bundler.loader().add(ClassLoaderSource.ROOT);
        assertEquals(Locale.UK, function.apply(bundler).locale());
    }
    
    
    static Stream<Function<Bundler, Bundle>> load_provider() {
        return Stream.of(
            bundler -> bundler.load(new Annotated(), Locale.UK),
            bundler -> bundler.load(Annotated.class, Locale.UK),
            bundler -> bundler.load(NAME, Locale.UK)
        );
    }
    
        
    static class Unannotated {
        
    }
    
    
    @ParameterizedTest
    @MethodSource("load_empty_provider")
    void load_empty(Function<Bundler, Bundle> function) {
        bundler.loader().add(ClassLoaderSource.ROOT);
        assertSame(Bundle.EMPTY, function.apply(bundler));
    }
    
    
    static Stream<Function<Bundler, Bundle>> load_empty_provider() {
        return Stream.of(
            bundler -> bundler.load(new Unannotated(), Locale.UK),
            bundler -> bundler.load(Unannotated.class, Locale.UK)
        );
    }
    
    
    @Test
    void loadFromServices() {
        Bundler.PROVIDERS.set(mock_service());
        
        var bundle = bundler.loadFromServices("loaded", Locale.UK);
        
        
        assertEquals(Locale.UK, bundle.locale());
        
        assertEquals(Locale.UK, bundler.cache().getIfPresent("loaded_en_GB").locale());
        assertEquals(Locale.ENGLISH, bundler.cache().getIfPresent("loaded_en").locale());
        assertEquals(Locale.ROOT, bundler.cache().getIfPresent("loaded").locale());
    }
    
    
    @Test
    void loadFromServices_nonexistent() {
        Bundler.PROVIDERS.set(mock_service());

        assertNull(bundler.loadFromServices("something", Locale.UK));
        assertEquals(0, bundler.cache().size());
    }
    
    
    @Test
    void loadFromServices_exception() {
        service = when(mock(ServiceLoader.class).iterator()).thenThrow(ServiceConfigurationError.class).getMock();
        Bundler.PROVIDERS.set(service);
        
        assertNull(bundler.loadFromServices("loaded", Locale.UK));
        assertEquals(0, bundler.cache().size());
    }
    
    
    @Test
    void cache() {
        bundler.cache("bundled", Locale.UK, chained);
        var bundle = bundler.cache().getIfPresent("bundled_en_GB");
        
        assertEquals(3, bundler.cache().size());
                
        assertEquals(Locale.UK, bundle.locale());
        assertEquals(Locale.ENGLISH, bundle.parent().locale());
        assertEquals(Locale.ROOT, bundle.parent().parent().locale());
    }
    
    
    @Test
    void cache_empty() {
        bundler.cache("empty", Locale.JAPAN, Bundle.empty(Locale.JAPAN, Bundle.EMPTY));
        var bundle = bundler.cache().getIfPresent("empty_ja_JP");
        
        assertEquals(1, bundler.cache().size());
        assertEquals(Locale.JAPAN, bundle.locale());
    }
    
    
    @Test
    void loadFromBundleLoader() {
        var parent = bundler.loadFromBundleLoader(NAME, Lists.reverse(BundleLoader.loader().parents(NAME, Locale.ENGLISH)), bundler.loader(), false);
        var bundle = bundler.loadFromBundleLoader(NAME, Lists.reverse(BundleLoader.loader().parents(NAME, Locale.UK)), bundler.loader(), false);
        
        assertEquals("Morning", bundle.find("hello"));
        assertEquals("Hey", bundle.parent().find("hello"));
        assertEquals("Hello", bundle.parent().parent().find("hello"));
        
        assertSame(parent, bundle.parent());
        assertEquals(Bundle.EMPTY, bundle.parent().parent().parent());
    }
    
    
    @Test
    void loadFromBundleLoader_empty() {
        var bundle = bundler.loadFromBundleLoader(NAME, Lists.reverse(BundleLoader.loader().parents(NAME, Locale.FRANCE)), bundler.loader(), false);
        
        assertEquals(Locale.FRANCE, bundle.locale());
        assertEquals(Locale.FRENCH, bundle.parent().locale());
        assertEquals(Locale.ROOT, bundle.parent().parent().locale());
    }
    
    
    @Test
    void loader() {
        assertSame(BundleLoader.loader(), CACHED.loader());
    }
    
}
