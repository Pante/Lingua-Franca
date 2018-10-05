<p align = "center">
  <img src = "https://i.imgur.com/GQoZzTm.png">
</p>

[![Build Status](https://travis-ci.org/Pante/Lingua-Franca.svg?branch=master)](https://travis-ci.org/Pante/Lingua-Franca)
[![Maintainability](https://api.codeclimate.com/v1/badges/3151e7bc7078007cad19/maintainability)](https://codeclimate.com/github/Pante/Lingua-Franca/maintainability)
[![Coverage](https://codecov.io/gh/Pante/Lingua-Franca/branch/master/graph/badge.svg)](https://codecov.io/gh/Pante/Lingua-Franca)
[![Stable Source Code](https://img.shields.io/badge/stable-branch-blue.svg)](https://github.com/Pante/Lingua-Franca/tree/stable)
[![releases-maven](https://img.shields.io/maven-metadata/v/https/repo.karuslabs.com/repository/lingua-franca-releases/com/karuslabs/lingua-franca/maven-metadata.xml.svg)](https://repo.karuslabs.com/service/rest/repository/browse/lingua-franca-releases/)
[![snapshots-maven](https://img.shields.io/maven-metadata/v/https/repo.karuslabs.com/repository/lingua-franca-nightly/com/karuslabs/lingua-franca/maven-metadata.xml.svg)](https://repo.karuslabs.com/service/rest/repository/browse/lingua-franca-nightly/)
[![javadoc](https://img.shields.io/badge/javadoc-1.0.0--SNAPSHOT-brightgreen.svg)](https://repo.karuslabs.com/repository/lingua-franca-project/1.0.0-SNAPSHOT/commons/apidocs/overview-summary.html)
[![Discord](https://img.shields.io/discord/140273735772012544.svg?logo=discord)](https://discord.gg/uE4C9NQ)

Java internationalisation that doesn't suck. Lingua-Franca is an annotation-driven internationalisation library that features semi-automated locale file(s) generation. In addition, the library supports properties, JSON and YAML files.

The master branch contains the bleeding edge, unreleased version of the project, please view the stable branch for the latest stable release. For more information, please see the [example project](https://github.com/Pante/Lingua-Franca/tree/stable/example/) and [wiki](https://github.com/Pante/Lingua-Franca/wiki/).

***
#### Java 10/11 Maven Artifact:
```XML

<!-- Release Builds -->
<repository>
  <id>chimera-releases</id>
  <url>https://repo.karuslabs.com/repository/lingua-franca-releases/</url>
</repository>

<!-- Nightly Builds -->
<repository>
  <id>chimera-snapshots</id>
  <url>https://repo.karuslabs.com/repository/lingua-franca-snapshots/</url>
</repository>

<dependencies>
  <dependency>
      <groupId>com.karuslabs</groupId>
      <artifactId>lingua-franca</artifactId>
      <version>1.0.0-SNAPSHOT</version>
  </dependency>
</dependencies>

<build>
    <plugin>
        <groupId>com.karuslabs</groupId>
        <artifactId>lingua-maven-plugin</artifactId>
        <version>1.0.0-SNAPSHOT</version>
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
#### Contributing
Please perform changes and submit pull requests from the master branch. Please adhere to the apparent style of the code you are editing.
