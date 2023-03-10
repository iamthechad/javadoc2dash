[![Build Status](https://travis-ci.org/iamthechad/javadoc2dash.svg)](https://travis-ci.org/iamthechad/javadoc2dash)
[![Coverage Status](https://coveralls.io/repos/github/iamthechad/javadoc2dash/badge.svg?branch=master)](https://coveralls.io/github/iamthechad/javadoc2dash?branch=master)
[![Download](https://api.bintray.com/packages/iamthechad/maven/javadoc2dash-api/images/download.svg) ](https://bintray.com/iamthechad/maven/javadoc2dash-api/_latestVersion)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
![Maintenance](https://img.shields.io/maintenance/yes/2019)
[![Badges](http://img.shields.io/:badges-5/5-ff6799.svg)](https://github.com/badges/badgerbadgerbadger)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Javadoc2Dash - Build Dash docsets from Javadoc](#javadoc2dash---build-dash-docsets-from-javadoc)
- [Gradle plugin](#gradle-plugin)
  - [Add the plugin to your project](#add-the-plugin-to-your-project)
  - [Specify settings](#specify-settings)
  - [Create the docset](#create-the-docset)
    - [Example](#example)
  - [Creating the docset feed](#creating-the-docset-feed)
    - [Example](#example-1)
- [Using the API](#using-the-api)
  - [Add dependencies to your project](#add-dependencies-to-your-project)
  - [Use the API](#use-the-api)
- [Using the CLI](#using-the-cli)
  - [Download the CLI](#download-the-cli)
    - [Running in a cloned repository](#running-in-a-cloned-repository)
    - [Running a release zip](#running-a-release-zip)
  - [Creating a docset](#creating-a-docset)
    - [Examples](#examples)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Javadoc2Dash - Build Dash docsets from Javadoc
 
This project is based off of https://github.com/Kapeli/javadocset. This is a Java-based solution so that Dash docsets
can be easily created from many environments, not just those that run OS X.

There are three ways to create Dash-compatible docsets from Javadoc using this project:

1. Use the Gradle plugin
1. Use the API
1. Use the CLI

# Gradle plugin

## Add the plugin to your project

Build script snippet for use in all Gradle versions:


    buildscript {
      repositories {
        maven {
          url "https://plugins.gradle.org/m2/"
        }
      }
      dependencies {
        classpath "gradle.plugin.com.megatome.javadoc2dash:j2d-gradle:1.1.0"
      }
    }

    apply plugin: "com.megatome.javadoc2dash"
    
Build script snippet for new, incubating, plugin mechanism introduced in Gradle 2.1:

    plugins {
      id "com.megatome.javadoc2dash" version "1.1.0"
    }

## Specify settings

    javadoc2dash {
      displayName = "My Cool Project"
    }
    
If no settings are provided, the plugin tries to use sensible defaults.

Setting Name | Type | Description | Default
-------------|------|-------------|--------
`docsetName` | `String` | File name of the created docset | `project.name`
`javadocRoot`| `File` | Location of the javadoc files | `${project.docsDir}/javadoc` 
`outputLocation`| `File` | Location to create the docset | `${project.buildDir}`
`displayName`| `String` | Name displayed in Dash | `project.name`
`keyword` | `String` | Keyword used for the docset in Dash | `project.name`
`iconFile` | `File` | File to be used as the docset icon | `null`
`javadocTask` | `String` | Name of the javadoc task that the `javadoc2dash` task will depend on | `javadoc`

**Some Caveats:**

* The `iconFile` should be a 32x32 PNG file, but the plugin does **not** verify this.
* You should only need to set the `javadocTask` property when the task you use to create Javadoc is non-standard. For example, there may be a task called `allJavadoc` in a multi-module
project to create an aggregated Javadoc. In this instance, `javadocTask` should be set to `allJavadoc` to ensure that the correct documentation is built before creating the docset.
* This plugin applies the `java` plugin to the project it's run under. This means that in a multi-module project, a top level task named `javadoc` cannot be created to aggregate the
subprojects' documentation. The `java` plugin creates a `javadoc` task, so a different name is required - perhaps `allJavadoc`.

## Create the docset

Create the docset with the `javadoc2dash` task.

### Example

    apply plugin: 'java'

    sourceCompatibility = 1.5
    version = '1.0'

    buildscript {
      repositories {
        maven {
          url "https://plugins.gradle.org/m2/"
        }
      }
      dependencies {
        classpath "gradle.plugin.com.megatome.javadoc2dash:j2d-gradle:1.1.0"
      }
    }

    apply plugin: "com.megatome.javadoc2dash"

    javadoc2dash {
      docsetName = "MyProject"
      displayName = "My Awesome Project"
      keyword = "mp"
    }
    
## Creating the docset feed

If you want to host your own docsets, you need to create a feed per the [Dash instructions](https://kapeli.com/docsets#dashdocsetfeed).

Creating feeds uses the `javadoc2dashfeed` task.

    javadoc2dashfeed {
      feedLocations = [ "http://someserver.com/feeds", "http://someotherserver.com/feeds" ]
    }
    
If no settings are provided, the plugin tries to use sensible defaults.

Setting Name | Type | Description | Default
-------------|------|-------------|--------
`feedName`   | `String` | File name to use for feed XML file | `project.name`
`feedVersion`| `String` | Version to use in feed XML file | `project.version`
`feedLocations` | `List<String>` | List of root URLs for hosting the docset | `null`

### Example

    apply plugin: 'java'

    sourceCompatibility = 1.5
    version = '1.0'

    buildscript {
      repositories {
        maven {
          url "https://plugins.gradle.org/m2/"
        }
      }
      dependencies {
        classpath "gradle.plugin.com.megatome.javadoc2dash:j2d-gradle:1.1.0"
      }
    }

    apply plugin: "com.megatome.javadoc2dash"

    javadoc2dash {
      docsetName = "MyProject"
      displayName = "My Awesome Project"
      keyword = "mp"
    }
    
    javadoc2dashfeed {
      feedName = "myproject"
      feedLocations = [ "http://someserver.com/feeds", "http://someotherserver.com/feeds" ]
    }
    
This will generate a `feed` directory in the `javadoc2dash.outputLocation` directory. This directory will contain an XML file describing the feed
(named `myproject.xml` in this case), and a compressed version of the docset (named `myproject.tgz` in this case).

For this example, the XML file will look like this:

    <entry>
      <version>1.0</version>
      <url>http://someserver.com/feeds/myproject.tgz</url>
      <url>http://someotherserver.com/feeds/myproject.tgz</url>
    </entry>
    
The XML file should be copied to a location where it can be [shared with Dash users](https://kapeli.com/docsets#sharedocsetfeed), and the `tgz` file copied to the locations specified in `feedLocations`.

# Using the API

## Add dependencies to your project

For Gradle:

    repositories {
      jcenter()
    }

    dependencies {
      compile "com.megatome.javadoc2dash:javadoc2dash-api:1.1.0"
    }
    
For Maven:

    <dependency>
      <groupId>com.megatome.javadoc2dash</groupId>
      <artifactId>javadoc2dash-api</artifactId>
      <version>1.1.0</version>
    </dependency>
    
## Use the API

    DocsetCreator.Builder builder = new DocsetCreator.Builder(docsetName, javadocLocation);
    // Optionally -
    builder.displayName("Some Name").keyword("keyword");
    DocsetCreator creator = builder.build();
    
    try {
      creator.makeDocset();
    } catch (BuilderException e) {
      // Something failed!
    }
    
# Using the CLI

## Download the CLI

Clone the project or grab the [latest release](https://github.com/iamthechad/javadoc2dash/releases). Running the utility will vary a bit depending on how you retrieve the project.

### Running in a cloned repository

Running the CLI directly from a Gradle task is not currently supported. A distribution must be created via `gradlew :j2d-cli:distZip` to create a zip file containing everything needed to run.
 
### Running a release zip

* Either download a release or create a distribution zip as outlined above. 
* Unzip the archive to a desired location.
* Open a terminal or command prompt and navigate to the unzipped directory.
* Navigate to the `bin` directory and run `./j2d-cli` (for \*NIX/OSX) or `j2d-cli.bat` (for Windows environments).
* You should see a usage message.

## Creating a docset

Docset creation requires at minimum two options: the name of the docset and the location of the Javadoc files to include in the docset.
  
    ./j2d-cli --name Sample --javadoc /some/path/to/apidoc
    
This will create a docset named Sample in the current directory. Docset creation can be customized with optional arguments:

* `--displayName`: Will set the name as shown in Dash. This is handy if you create a docset with name `SampleProject` but display name `Sample Project` instead.
    * This setting will default to the value of `--name` if omitted.
* `--keyword`: Will set the keyword used to search in Dash. You could set the keyword for `SampleProject` to `sp`, for example.
    * This setting will default to the value of `--name` if omitted.
* `--icon`: Specify an icon to be used for the docset. Should be a 32x32 PNG, but this tool **does not verify the file's content**.
    * No icon will be used if this is omitted.
* `--out`: Specify a directory to create the docset in.
    * The docset will be created in the current directory if omitted.
    
### Examples

Bare minimum: `j2d-cli --name Sample --javadoc /path/to/apidoc`

Full options: `j2d-cli --name Sample --javadoc /path/to/apidoc --displayName "Awesome Sample API" --keyword asa --iconFile /path/to/icon.png --out /path/to/output`

Abbreviated options. Most command-line options can be abbreviated. `j2d-cli -n Sample -j /path/to/apidoc -d "Awesome Sample API" -k asa -i /path/to/icon.png -o /path/to/output`

# Custom HTML to DashDocset generation

The plugin supports the implementation of a custom Dash Docset where the search properties can be generated at will. The default JavaDoc DashDocset is basically a specific implementation. To create a custom docset out of any HTML you need to implement the DocSetParserInterface and the MatchTypeInterface.

An example for JSDoc3 can be found at [https://github.com/i-net-software/jsdoc-dash-docset](https://github.com/i-net-software/jsdoc-dash-docset)