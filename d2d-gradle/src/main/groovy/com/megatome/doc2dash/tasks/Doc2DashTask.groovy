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
package com.megatome.doc2dash.tasks

import com.megatome.d2d.DocsetCreator
import com.megatome.d2d.support.DocSetParserInterface
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class Doc2DashTask extends DefaultTask {
    @Input String docsetName
    @Input File docRoot
    @Input File outputLocation
    @Input String displayName
    @Input String keyword

    @Input
    @Optional
    File iconFile

    @Input
    @Optional
    DocSetParserInterface implementation

    @Input
    @Optional
    String type

    Doc2DashTask() {
        this.description = 'Create a Dash docset from Javadoc';
        group = 'Doc2Dash'
    }

    @TaskAction
    void start() {
        withExceptionHandling {

            DocsetCreator.Builder builder = new DocsetCreator.Builder(docsetName, docRoot)
                .displayName(displayName)
                .keyword(keyword)
                .outputDirectory(outputLocation)
                .iconFile(iconFile)
                .implementation(type)
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
