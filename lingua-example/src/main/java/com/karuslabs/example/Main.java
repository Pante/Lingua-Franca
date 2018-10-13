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
package com.karuslabs.example;

import com.karuslabs.lingua.franca.Bundle;
import com.karuslabs.lingua.franca.Bundler;
import com.karuslabs.lingua.franca.Locales;
import com.karuslabs.lingua.franca.annotations.ClassLoaderSources;
import com.karuslabs.lingua.franca.annotations.ModuleSources;
import com.karuslabs.lingua.franca.annotations.SystemSources;
import com.karuslabs.lingua.franca.sources.ClassLoaderSource;
import com.karuslabs.lingua.franca.sources.ModuleSource;
import com.karuslabs.lingua.franca.template.Templates;
import com.karuslabs.lingua.franca.template.annotations.In;
import com.karuslabs.lingua.franca.template.annotations.Platform;

import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

import org.checkerframework.checker.nullness.qual.Nullable;
import com.karuslabs.lingua.franca.annotations.Namespace;


public class Main {
    
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Puns PUNS = new Puns();
    private static boolean reload = false;
    
    
    public static void main(String... args) {
        Bundler.bundler().loader().add(PUNS);
        Bundler.bundler().loader().add(new ClassLoaderSource(""), new ModuleSource(""));
        
        Templates.fromPlatforms(PUNS);
        
        while (true) {
            System.out.println("\nEnter the locale to view puns. (en_GB, es, fr_FR, ja-JP, zh)");
            System.out.println("Enter 'custom_bundle' to view custom BundleProvider implementation");
            System.out.println("Enter 'reload' to force reloading.");
            System.out.println("Enter 'exit' to exit.");
            
            var entered = SCANNER.nextLine();
            
            System.out.println("\n");
            
            switch (entered.toLowerCase()) {
                case "custom_bundle":
                    customBundle();
                    break;
                
                case "reload":
                    reload = !reload;
                    System.out.println("Reload: " + reload);
                    break;
                
                case "exit":
                    return;
                    
                default:
                    PUNS.make(entered, reload);
            }
        }
    }
    
    private static void customBundle() {
        Bundle bundle = Bundler.bundler().load("custom_bundle", Locale.ITALY);
        System.out.println(bundle.get("description").orElse("I forgot the description!"));
    }
    
}


@Namespace("puns")
@ClassLoaderSources({"puns/classloader"})
@ModuleSources({"puns/module"})
@SystemSources({"./"})
@Platform(template = @In(embedded = "puns/classloader/puns.yml"), locales = {"EOGWEGG", "fr_FR", "ja_JP"}, destination = "./")
class Puns {
    
    private final Bundler bundler = Bundler.bundler();
    
    
    public void make(String tag, boolean reload) {
        Locale locale = Locales.of(tag);
        
        Bundle bundle = null;
        if (reload) {
            bundle = bundler.reload(this, locale);
            
        } else {
            bundle = bundler.load(this, locale);
        }
        
        Optional<String> title = bundle.get("title", locale.toLanguageTag());
        System.out.println(title.orElse("Terrible Puns"));
        
        @Nullable String question = bundle.find("first.question");
        Optional<String> answer = bundle.get("first.answer");
        
        monologue(question, answer.orElse("I forgot the answer!"));
        
        
        question = bundle.find("second.question");
        answer = bundle.get("second.answer");
        
        monologue(question, answer.orElse("I forgot the answer!"));
        
        
        @Nullable String response = bundle.find("responses[0]", System.getProperty("user.name"));
        System.out.println(response);
        
        @Nullable String[] responses = bundle.messagesIfPresent("responses");
        System.out.println(responses[1]);
    }
    
    private void monologue(String question, String answer) {
        System.out.println(question);
        System.out.println(answer);
        System.out.println("\n");
    }
    
}
