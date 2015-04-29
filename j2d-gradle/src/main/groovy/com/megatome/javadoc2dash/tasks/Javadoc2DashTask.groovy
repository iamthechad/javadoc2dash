package com.megatome.javadoc2dash.tasks

import com.megatome.j2d.Builder
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class Javadoc2DashTask extends DefaultTask {
    @Input String docsetName
    @Input File javadocRoot
    @Input File outputLocation
    @Input String displayName
    @Input String keyword
    @Input File iconFile

    Javadoc2DashTask() {
        this.description = 'Create a Dash docset from Javadoc';
        group = 'Javadoc2Dash'
    }

    @TaskAction
    void start() {
        withExceptionHandling {
            logger.quiet "Docset Name: $docsetName"
            logger.quiet "Javadoc Root: $javadocRoot"
            logger.quiet "Display Name: $displayName"
            logger.quiet "Keyword: $keyword"
            logger.quiet "Icon File: $iconFile"
            logger.quiet "Output Location: $outputLocation"
            //Builder builder = new Builder(docsetName, javadocRoot, displayName, keyword, iconFile, outputLocation)
            //builder.build()
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
