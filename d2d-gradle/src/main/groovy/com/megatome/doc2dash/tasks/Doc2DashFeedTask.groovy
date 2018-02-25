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

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class Doc2DashFeedTask extends DefaultTask {
    @Input String feedName
    @Input String docsetFile
    @Input String feedVersion
    @Input List feedLocations
    @Input File outputLocation

    Doc2DashFeedTask() {
        this.description = 'Create a Dash feed from a docset';
        group = 'Doc2Dash'
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
