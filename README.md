[![Build Status](http://img.shields.io/travis/iamthechad/javadoc2dash.svg)](https://travis-ci.org/iamthechad/javadoc2dash)
[![Stories in Ready](https://badge.waffle.io/iamthechad/javadoc2dash.png?label=ready&title=Ready)](http://waffle.io/iamthechad/javadoc2dash)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Badges](http://img.shields.io/:badges-4/4-ff6799.svg)](https://github.com/badges/badgerbadgerbadger)

# Javadoc2Dash - Build Dash docsets from Javadoc
 
This project is based off of https://github.com/Kapeli/javadocset. This is a Java-based solution so that Dash docsets
can be easily created from many environments, not just those that run OS X.

## Gradle plugin

Coming soon, once I get things posted to the right places on the interweb.

## Running the utility

Clone the project or grab the [latest release](https://github.com/iamthechad/javadoc2dash/releases). Running the utility will vary a bit depending on how you retrieve the project.

### Running in a cloned repository

* Open a terminal or command prompt, depending on your OS
* Within the cloned project directory, run `./gradlew :j2d-cli:run` (for \*NIX/OSX), or `gradlew.bat :j2d-cli:run` (for Windows environments). 
* The Gradle wrapper will bootstrap itself and you should see a usage message.

Alternatively, run `gradlew :j2d-cli:distZip` to create a zip file containing everything needed to run. See the next step for more information.
 
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
