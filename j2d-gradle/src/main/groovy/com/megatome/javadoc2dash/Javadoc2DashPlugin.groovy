package com.megatome.javadoc2dash

import com.megatome.javadoc2dash.tasks.Javadoc2DashTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class Javadoc2DashPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.plugins.apply(JavaPlugin)
        project.extensions.create("javadoc2dash", Javadoc2DashPluginExtension, project)
        addTasks(project)
    }

    private void addTasks(Project project) {
        def extension = project.extensions.findByName("javadoc2dash")
        project.task('javadoc2dash', type: Javadoc2DashTask, dependsOn: 'javadoc') {
            conventionMapping.docsetName = { extension.docsetName }
            conventionMapping.displayName = { extension.displayName }
            conventionMapping.keyword = { extension.keyword }
            conventionMapping.javadocRoot = { extension.javadocRoot }
            conventionMapping.outputLocation = { extension. outputLocation }
        }
    }
}

class Javadoc2DashPluginExtension {
    String docsetName
    File javadocRoot
    File outputLocation
    String displayName
    String keyword
    File iconFile

    Javadoc2DashPluginExtension(Project project) {
        docsetName = project.name
        displayName = project.name
        keyword = project.name
        javadocRoot = project.file("${project.docsDir}/javadoc")
        outputLocation = project.file("${project.buildDir}")
    }
}