<p align = "center">
  <img src = "https://i.imgur.com/GQoZzTm.png">
</p>

[![Travis-CI](https://img.shields.io/travis/pante/lingua-franca/master?logo=travis)](https://travis-ci.org/Pante/Lingua-Franca)
[![Funding](https://img.shields.io/badge/%F0%9F%A4%8D%20-sponsorship-ff69b4?style=flat-square)](https://github.com/sponsors/Pante)
[![Maintainability](https://api.codeclimate.com/v1/badges/3151e7bc7078007cad19/maintainability)](https://codeclimate.com/github/Pante/Lingua-Franca/maintainability)
[![Coverage](https://codecov.io/gh/Pante/Lingua-Franca/branch/master/graph/badge.svg)](https://codecov.io/gh/Pante/Lingua-Franca)
[![Stable Branch](https://img.shields.io/badge/stable-branch-blue.svg)](https://github.com/Pante/Lingua-Franca/tree/stable)
[![releases-maven](https://img.shields.io/maven-metadata/v/https/repo.karuslabs.com/repository/lingua-franca-releases/com/karuslabs/lingua-franca/maven-metadata.xml.svg)](https://repo.karuslabs.com/service/rest/repository/browse/lingua-franca-releases/)
[![snapshots-maven](https://img.shields.io/maven-metadata/v/https/repo.karuslabs.com/repository/lingua-franca-nightly/com/karuslabs/lingua-franca/maven-metadata.xml.svg)](https://repo.karuslabs.com/service/rest/repository/browse/lingua-franca-nightly/)
[![javadoc](https://img.shields.io/badge/javadoc-1.0.6-brightgreen.svg)](https://repo.karuslabs.com/repository/lingua-franca-project/1.0.6/lingua-franca/apidocs/overview-summary.html)
[![Discord](https://img.shields.io/discord/140273735772012544.svg?logo=discord)](https://discord.gg/uE4C9NQ)

Lingua Franca is an annotation-driven internationalisation library that features semi-automated locale file(s) generation. In addition, the library supports properties, JSON and YAML files.

The master branch contains the bleeding edge, unreleased version of the project. Please view the stable branch for the latest stable release. For more information, please see the [example project](https://github.com/Pante/Lingua-Franca/tree/stable/lingua-example/) and [wiki](https://github.com/Pante/Lingua-Franca/wiki/).

***

#### Feature Comparison:

|                          | Plain Java | Lingua Franca |
|--------------------------|:----------:|:-------------:|
| Annotations              |      ❌     |       ✔       |
| Formatted Messages       |      ❌     |       ✔       |
| Optional Messages        |      ❌     |       ✔       |
| Embedded Resources       |      ✔     |       ✔       |
| Runtime Resources        |      ❌     |       ✔       |
| Locale File Generation   |      ❌     |       ✔       |
| Locale File (properties) |      ✔     |       ✔       |
| Locale File (JSON)       |      ❌     |       ✔       |
| Locale File (YAML)       |      ❌     |       ✔       |

***

**Lingua Franca 1.0.2 and above requires Java 11; prior versions require Java 10.** 

#### Java 11+ Maven Artifact:
```XML

<!-- Release Builds -->
<repository>
  <id>lingua-releases</id>
  <url>https://repo.karuslabs.com/repository/lingua-franca-releases/</url>
</repository>

<!-- Nightly Builds -->
<repository>
  <id>lingua-snapshots</id>
  <url>https://repo.karuslabs.com/repository/lingua-franca-snapshots/</url>
</repository>

<dependencies>
  <dependency>
      <groupId>com.karuslabs</groupId>
      <artifactId>lingua-franca</artifactId>
      <version>1.0.6</version>
  </dependency>
</dependencies>

<build>
    <!-- Optional but highly recommended -->
    <plugin>
        <groupId>com.karuslabs</groupId>
        <artifactId>lingua-maven-plugin</artifactId>
        <version>1.0.6</version>
        <executions>
            <execution>
                <goals>
                    <!-- Lints the usage of Lingua Franca annotations in the project -->
                    <goal>lint</goal>
                    <!-- Generates the embedded template locale file(s) at compilation -->
                    <goal>generate</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</build>
```
***

#### Getting Started

```JAVA
@Namespace("messages")
@ClassLoaderSources({"translations"})
class Foo {
    
    Bundler bundler = Bundler.bundler();
	
    void register() {
        bundler.loader().add(this); // Tells Lingua-Franca to look in the src/main/resources/translations folder
    }
	
    void obtain() {
        Bundle bundle = bundler.load(this, Locale.UK); // Returns messages_en.yml in the src/main/resources/translations folder
		
	String value = bundle.find("path.to.value"); // "value {0}"
	String formatted_value = bundle.find("path.to.value", "formatted"); // "value formatted"
    }
    
}
```

For more information please see the [quick start](https://github.com/Pante/Lingua-Franca/wiki/quick-start).
***

#### Contributing
Please perform changes and submit pull requests from the master branch. Please adhere to the apparent style of the code you are editing.
