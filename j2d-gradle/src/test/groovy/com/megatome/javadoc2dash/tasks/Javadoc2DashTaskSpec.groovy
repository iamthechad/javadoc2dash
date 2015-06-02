package com.megatome.javadoc2dash.tasks

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class Javadoc2DashTaskSpec extends Specification{
    static final TASK_NAME = "javadoc2dash"
    static final PLUGIN_ID = 'com.megatome.javadoc2dash'

    Project project

    def setup() {
        ProjectBuilder builder = ProjectBuilder.builder()
        builder.withProjectDir(new File("src/test/resources/TestProject"))
        project = builder.build()
    }

    def "Execute task with bad information"() {
        expect:
            project.tasks.findByName(TASK_NAME) == null
        when:
            project.apply plugin: PLUGIN_ID
            project.javadoc2dash {
                docsetName = null
                javadocRoot = null
            }
            Task j2dTask = project.tasks.findByName(TASK_NAME)
            j2dTask.start()
        then:
            thrown(GradleException)
    }
}
