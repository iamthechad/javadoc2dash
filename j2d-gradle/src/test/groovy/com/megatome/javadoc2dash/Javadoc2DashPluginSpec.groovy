package com.megatome.javadoc2dash

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class Javadoc2DashPluginSpec extends Specification {
    static final TASK_NAME = "javadoc2dash"
    static final EXTENSION_NAME = "javadoc2dash"
    static final PLUGIN_ID = 'com.megatome.javadoc2dash'

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
            project.extensions.findByName(EXTENSION_NAME) != null
            Task j2dTask = project.tasks.findByName(TASK_NAME)
            j2dTask != null
            j2dTask.docsetName == project.name
            j2dTask.displayName == project.name
            j2dTask.keyword == project.name
            j2dTask.javadocRoot == project.file("${project.docsDir}/javadoc")
            j2dTask.outputLocation == project.file("${project.buildDir}/javadoc2dash")
            j2dTask.iconFile == null
    }

    def "Apply plugin and set all extension values"() {
        expect:
            project.tasks.findByName(TASK_NAME) == null
        when:
            project.apply plugin: PLUGIN_ID

            project.javadoc2dash {
                docsetName = "Project Name"
                displayName = "Display Name"
                keyword = "Keyword"
                javadocRoot = project.file("${project.docsDir}/docs")
                outputLocation = project.file("${project.buildDir}/docsets")
                iconFile = project.file("icon.png")
            }
        then:
            project.plugins.hasPlugin(JavaPlugin)
            project.extensions.findByName(EXTENSION_NAME) != null
            Task j2dTask = project.tasks.findByName(TASK_NAME)
            j2dTask != null
            j2dTask.docsetName == "Project Name"
            j2dTask.displayName == "Display Name"
            j2dTask.keyword == "Keyword"
            j2dTask.javadocRoot == project.file("${project.docsDir}/docs")
            j2dTask.outputLocation == project.file("${project.buildDir}/docsets")
            j2dTask.iconFile == project.file("icon.png")
    }

    def "Apply plugin and set some extension values"() {
        expect:
        project.tasks.findByName(TASK_NAME) == null
        when:
        project.apply plugin: PLUGIN_ID

        project.javadoc2dash {
            displayName = "Display Name"
            keyword = "Keyword"
            outputLocation = project.file("${project.buildDir}/docsets")
        }
        then:
        project.plugins.hasPlugin(JavaPlugin)
        project.extensions.findByName(EXTENSION_NAME) != null
        Task j2dTask = project.tasks.findByName(TASK_NAME)
        j2dTask != null
        j2dTask.docsetName == project.name
        j2dTask.displayName == "Display Name"
        j2dTask.keyword == "Keyword"
        j2dTask.javadocRoot == project.file("${project.docsDir}/javadoc")
        j2dTask.outputLocation == project.file("${project.buildDir}/docsets")
        j2dTask.iconFile == null
    }
}
