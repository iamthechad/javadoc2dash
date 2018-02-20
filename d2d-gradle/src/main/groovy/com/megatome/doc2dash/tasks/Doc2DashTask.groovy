package com.megatome.doc2dash.tasks

import com.megatome.d2d.DocsetCreator
import com.megatome.d2d.support.DocSetParserInterface
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction

class Doc2DashTask extends DefaultTask {
    @Input String docsetName
    @Input File javadocRoot
    @Input File outputLocation
    @Input String displayName
    @Input String keyword

    @Input
    @Optional
    File iconFile

    @Input
    @Optional
    DocSetParserInterface implementation

    Doc2DashTask() {
        this.description = 'Create a Dash docset from Javadoc';
        group = 'Doc2Dash'
    }

    @TaskAction
    void start() {
        withExceptionHandling {
            DocsetCreator.Builder builder = new DocsetCreator.Builder(docsetName, javadocRoot)
                .displayName(displayName)
                .keyword(keyword)
                .outputDirectory(outputLocation)
                .iconFile(iconFile)
                .implementation(implementation)
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
