/*
 * Copyright 2018 Megatome Technologies, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.megatome.doc2dash

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class Javadoc2DashPluginSpec extends Specification {
    static final TASK_NAME = "doc2dash"
    static final EXTENSION_NAME = "doc2dash"
    static final PLUGIN_ID = 'com.megatome.doc2dash'

    Project project

    def setup() {
        ProjectBuilder builder = ProjectBuilder.builder()
        builder.withProjectDir(new File("src/test/resources/TestProject"))
        project = builder.build()
    }

    def "Apply plugin and use default extension values"() {
        expect:
            project.tasks.findByName(TASK_NAME) == null
        when:
            project.apply plugin: PLUGIN_ID
        then:
            project.plugins.hasPlugin(JavaPlugin)
            def baseExtension = project.extensions.findByName(EXTENSION_NAME)
            baseExtension != null
            Task d2dTask = project.tasks.findByName(TASK_NAME)
            d2dTask != null
            d2dTask.docsetName == project.name
            d2dTask.displayName == project.name
            d2dTask.keyword == project.name
            d2dTask.docRoot == project.file("${project.docsDir}/javadoc")
            d2dTask.outputLocation == project.file("${project.buildDir}/doc2dash")
            d2dTask.iconFile == null
            baseExtension.docTask == "javadoc"
    }

    def "Apply plugin and set all extension values"() {
        expect:
            project.tasks.findByName(TASK_NAME) == null
        when:
            project.apply plugin: PLUGIN_ID

            project.doc2dash {
                docsetName = "Project Name"
                displayName = "Display Name"
                keyword = "Keyword"
                docRoot = project.file("${project.docsDir}/docs")
                outputLocation = project.file("${project.buildDir}/docsets")
                iconFile = project.file("icon.png")
                docTask = "allJavadoc"
            }
        then:
            project.plugins.hasPlugin(JavaPlugin)
            def baseExtension = project.extensions.findByName(EXTENSION_NAME)
            baseExtension != null
            Task d2dTask = project.tasks.findByName(TASK_NAME)
            d2dTask != null
            d2dTask.docsetName == "Project Name"
            d2dTask.displayName == "Display Name"
            d2dTask.keyword == "Keyword"
            d2dTask.docRoot == project.file("${project.docsDir}/docs")
            d2dTask.outputLocation == project.file("${project.buildDir}/docsets")
            d2dTask.iconFile == project.file("icon.png")
            baseExtension.docTask == "allJavadoc"
    }

    def "Apply plugin and set some extension values"() {
        expect:
        project.tasks.findByName(TASK_NAME) == null
        when:
        project.apply plugin: PLUGIN_ID

        project.doc2dash {
            displayName = "Display Name"
            keyword = "Keyword"
            outputLocation = project.file("${project.buildDir}/docsets")
        }
        then:
        project.plugins.hasPlugin(JavaPlugin)
        project.extensions.findByName(EXTENSION_NAME) != null
        Task d2dTask = project.tasks.findByName(TASK_NAME)
        d2dTask != null
        d2dTask.docsetName == project.name
        d2dTask.displayName == "Display Name"
        d2dTask.keyword == "Keyword"
        d2dTask.docRoot == project.file("${project.docsDir}/javadoc")
        d2dTask.outputLocation == project.file("${project.buildDir}/docsets")
        d2dTask.iconFile == null
    }
}
