package com.megatome.doc2dash

import com.megatome.doc2dash.tasks.Doc2DashFeedTask
import com.megatome.doc2dash.tasks.Doc2DashTask
import com.megatome.d2d.support.DocSetParserInterface
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Compression
import org.gradle.api.tasks.bundling.Tar

class Doc2DashPlugin implements Plugin<Project> {
    private static final String EXTENSION_NAME = "doc2dash"
    private static final String FEED_EXTENSION_NAME = "doc2dashfeed"
    private static final String FEED_LOCATION = "feed"

    @Override
    void apply(Project project) {
        project.plugins.apply(JavaPlugin)
        project.extensions.create(EXTENSION_NAME, Doc2DashPluginExtension, project)
        project.extensions.create(FEED_EXTENSION_NAME, Doc2DashFeedPluginExtension, project)
        addTasks(project)
    }

    private void addTasks(Project project) {
        def baseExtension = project.extensions.findByName(EXTENSION_NAME)
        project.task('doc2dash', type: Doc2DashTask) {
            dependsOn({ baseExtension.javadocTask })
            conventionMapping.docsetName = { baseExtension.docsetName }
            conventionMapping.displayName = { baseExtension.displayName }
            conventionMapping.keyword = { baseExtension.keyword }
            conventionMapping.javadocRoot = { baseExtension.javadocRoot }
            conventionMapping.outputLocation = { baseExtension.outputLocation }
            conventionMapping.iconFile = { baseExtension.iconFile }
            conventionMapping.implementation = { baseExtension.implementation }
        }

        def feedExtension = project.extensions.findByName(FEED_EXTENSION_NAME)
        def feedLocation = project.file("${baseExtension.outputLocation}/${FEED_LOCATION}")

        project.task('doc2dashtar', type: Tar) {
            dependsOn({ 'doc2dash' })
            description = 'Create a tarred version of the docset.';
            group = 'Doc2Dash'
            conventionMapping.baseName = { feedExtension.feedName }
            version = null
            conventionMapping.extension = { "tgz" }
            conventionMapping.compression = { Compression.GZIP }
            from project.files("${baseExtension.outputLocation}") {
                exclude FEED_LOCATION
            }
            conventionMapping.destinationDir = { feedLocation }
        }

        project.task('doc2dashfeed', type: Doc2DashFeedTask) {
            dependsOn({ 'doc2dashtar' })
            conventionMapping.feedName = { feedExtension.feedName }
            conventionMapping.docsetFile = { feedExtension.feedName + ".tgz" }
            conventionMapping.outputLocation = { feedLocation }
            conventionMapping.feedVersion = { feedExtension.feedVersion }
            conventionMapping.feedLocations = { feedExtension.feedLocations }
        }
    }
}

class Doc2DashPluginExtension {
    String docsetName
    File javadocRoot
    File outputLocation
    String displayName
    String keyword
    File iconFile
    String javadocTask
    DocSetParserInterface implementation

    Doc2DashPluginExtension(Project project) {
        docsetName = project.name
        displayName = project.name
        keyword = project.name
        javadocRoot = project.file("${project.docsDir}/javadoc")
        outputLocation = project.file("${project.buildDir}/doc2dash")
        iconFile = null
        implementation = null
        javadocTask = "javadoc"
    }
}

class Doc2DashFeedPluginExtension {
    String feedName
    String feedVersion
    List feedLocations

    Doc2DashFeedPluginExtension(Project project) {
        feedName = project.name
        feedVersion = project.version
        feedLocations = null
    }
}