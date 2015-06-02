package com.megatome.javadoc2dash

import com.megatome.javadoc2dash.tasks.Javadoc2DashFeedTask
import com.megatome.javadoc2dash.tasks.Javadoc2DashTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Compression
import org.gradle.api.tasks.bundling.Tar

class Javadoc2DashPlugin implements Plugin<Project> {
    private static final String EXTENSION_NAME = "javadoc2dash"
    private static final String FEED_EXTENSION_NAME = "javadoc2dashfeed"
    private static final String FEED_LOCATION = "feed"

    @Override
    void apply(Project project) {
        project.plugins.apply(JavaPlugin)
        project.extensions.create(EXTENSION_NAME, Javadoc2DashPluginExtension, project)
        project.extensions.create(FEED_EXTENSION_NAME, Javadoc2DashFeedPluginExtension, project)
        addTasks(project)
    }

    private void addTasks(Project project) {
        def baseExtension = project.extensions.findByName(EXTENSION_NAME)
        project.task('javadoc2dash', type: Javadoc2DashTask) {
            dependsOn({ baseExtension.javadocTask })
            conventionMapping.docsetName = { baseExtension.docsetName }
            conventionMapping.displayName = { baseExtension.displayName }
            conventionMapping.keyword = { baseExtension.keyword }
            conventionMapping.javadocRoot = { baseExtension.javadocRoot }
            conventionMapping.outputLocation = { baseExtension.outputLocation }
            conventionMapping.iconFile = { baseExtension.iconFile }
        }

        def feedExtension = project.extensions.findByName(FEED_EXTENSION_NAME)
        def feedLocation = project.file("${baseExtension.outputLocation}/${FEED_LOCATION}")

        project.task('javadoc2dashtar', type: Tar) {
            dependsOn({ 'javadoc2dash' })
            description = 'Create a tarred version of the docset.';
            group = 'Javadoc2Dash'
            conventionMapping.baseName = { feedExtension.feedName }
            version = null
            conventionMapping.extension = { "tgz" }
            conventionMapping.compression = { Compression.GZIP }
            from project.files("${baseExtension.outputLocation}") {
                exclude FEED_LOCATION
            }
            conventionMapping.destinationDir = { feedLocation }
        }

        project.task('javadoc2dashfeed', type: Javadoc2DashFeedTask) {
            dependsOn({ 'javadoc2dashtar' })
            conventionMapping.feedName = { feedExtension.feedName }
            conventionMapping.docsetFile = { feedExtension.feedName + ".tgz" }
            conventionMapping.outputLocation = { feedLocation }
            conventionMapping.feedVersion = { feedExtension.feedVersion }
            conventionMapping.feedLocations = { feedExtension.feedLocations }
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
    String javadocTask

    Javadoc2DashPluginExtension(Project project) {
        docsetName = project.name
        displayName = project.name
        keyword = project.name
        javadocRoot = project.file("${project.docsDir}/javadoc")
        outputLocation = project.file("${project.buildDir}/javadoc2dash")
        iconFile = null
        javadocTask = "javadoc"
    }
}

class Javadoc2DashFeedPluginExtension {
    String feedName
    String feedVersion
    List feedLocations

    Javadoc2DashFeedPluginExtension(Project project) {
        feedName = project.name
        feedVersion = project.version
        feedLocations = null
    }
}