package com.sdl.dxa.builder.maven

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class MavenBuildTaskTest {
    MavenBuildTask task
    Project project

    @Before
    void before() {
        project = ProjectBuilder.builder().build()
        task = project.task('maven.build', type: MavenBuildTask) as MavenBuildTask
        task.run()
    }

    @Test
    void canAddTaskToProject() {
        assertTrue(task instanceof MavenBuildTask)
    }

    @Test
    void shouldBuildTaskForCustomCommand() {
        'assert'("mvn hello ${Defaults.MAVEN_PROPERTIES}", '')(task.buildTask('>> hello', {}, true))
        'assert'("mvn hello ${Defaults.MAVEN_PROPERTIES}", '')(task.buildTask('    >    >     hello    ', {}, true))
        'assert'("mvn hello ${Defaults.MAVEN_PROPERTIES}", '')(task.buildTask('>>hello', {}, true))
    }

    @Test
    void shouldBuildTaskForCustomCommandForProject() {
        'assert'("mvn hello ${Defaults.MAVEN_PROPERTIES}", 'project')(task.buildTask('> project > hello', {}, true))
        'assert'("mvn hello ${Defaults.MAVEN_PROPERTIES}", 'project')(task.buildTask('       >         project           >         hello        ', {
        }, true))
        'assert'("mvn hello ${Defaults.MAVEN_PROPERTIES}", 'project')(task.buildTask('>project>hello', {}, true))
    }

    @Test
    void shouldBuildTaskForProject() {
        'assert'("mvn install ${task.mavenProperties}", 'project')(task.buildTask('project', {}, true))
        'assert'("mvn install ${task.mavenProperties}", 'project')(task.buildTask('  project  ', {}, true))
    }

    private static Closure 'assert'(String command, name) {
        return { buildTask ->
            assertEquals(command, buildTask.commandToExecute)
            assertEquals(name, buildTask.name)
        }
    }
}
