package com.megatome.javadoc2dash.tasks

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class Javadoc2DashFeedTask extends DefaultTask {
    @Input String feedName
    @Input String docsetFile
    @Input String feedVersion
    @Input List feedLocations
    @Input File outputLocation

    Javadoc2DashFeedTask() {
        this.description = 'Create a Dash feed from a docset';
        group = 'Javadoc2Dash'
    }

    @TaskAction
    void start() {
        withExceptionHandling {
            if (!outputLocation.exists()) {
                project.mkdir(outputLocation)
            }

            def fw = new FileWriter(project.file("${outputLocation}/${feedName}.xml"))
            def xml = new MarkupBuilder(fw)

            xml.entry() {
                version(feedVersion)
                feedLocations.each {
                    def feedUrl = it
                    if (!it.endsWith('/')) {
                        feedUrl += '/'
                    }
                    url(feedUrl + docsetFile)
                }
            }
        }
    }

    private static void withExceptionHandling(Closure c) {
        try {
            c()
        } catch (Exception e) {
            throw new GradleException(e.message);
        }
    }
}
