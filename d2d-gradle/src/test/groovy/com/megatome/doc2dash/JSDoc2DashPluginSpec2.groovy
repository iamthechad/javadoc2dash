package com.megatome.doc2dash

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class JSDoc2DashPluginSpec extends Specification {
    static final TASK_NAME = "doc2dash"
    static final EXTENSION_NAME = "doc2dash"
    static final PLUGIN_ID = 'com.megatome.doc2dash'

    Project project

    def setup() {
        ProjectBuilder builder = ProjectBuilder.builder()
        builder.withProjectDir(new File("src/test/resources/JSDoc"))
        project = builder.build()
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
                javadocRoot = project.file("src/test/resources/JSDoc")
                outputLocation = project.file("${project.buildDir}/docsets")
                iconFile = project.file("icon.png")
                javadocTask = "jsdoc"
                type = "jsdoc"
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
            d2dTask.javadocRoot == project.file("src/test/resources/JSDoc")
            d2dTask.outputLocation == project.file("${project.buildDir}/docsets")
            d2dTask.iconFile == project.file("icon.png")
            baseExtension.javadocTask == "jsdoc"
            d2dTask.type == "jsdoc"
    }
}
