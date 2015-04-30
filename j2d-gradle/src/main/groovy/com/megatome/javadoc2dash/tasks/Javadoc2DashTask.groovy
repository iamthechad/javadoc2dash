package com.megatome.javadoc2dash.tasks

import com.megatome.j2d.DocsetCreator
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction

class Javadoc2DashTask extends DefaultTask {
    @Input String docsetName
    @Input File javadocRoot
    @Input File outputLocation
    @Input String displayName
    @Input String keyword

    @Input
    @Optional
    File iconFile

    Javadoc2DashTask() {
        this.description = 'Create a Dash docset from Javadoc';
        group = 'Javadoc2Dash'
    }

    @TaskAction
    void start() {
        withExceptionHandling {
            DocsetCreator.Builder builder = new DocsetCreator.Builder(docsetName, javadocRoot)
                .displayName(displayName)
                .keyword(keyword)
                .outputDirectory(outputLocation)
                .iconFile(iconFile)
            DocsetCreator creator = builder.build()
            creator.makeDocset()
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
