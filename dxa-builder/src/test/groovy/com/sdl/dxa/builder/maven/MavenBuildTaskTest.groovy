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
    public void before() {
        project = ProjectBuilder.builder().build()
        task = project.task('maven.build', type: MavenBuildTask) as MavenBuildTask
        task.run()
    }

    @Test
    public void canAddTaskToProject() {
        assertTrue(task instanceof MavenBuildTask)
    }

    @Test
    public void shouldBuildTaskForCustomCommand() {
        'assert'('hello', '')(task.buildTask('>> hello', {}, true))
        'assert'('hello', '')(task.buildTask('    >    >     hello    ', {}, true))
        'assert'('hello', '')(task.buildTask('>>hello', {}, true))
    }

    @Test
    public void shouldBuildTaskForCustomCommandForProject() {
        'assert'('hello', 'project')(task.buildTask('> project > hello', {}, true))
        'assert'('hello', 'project')(task.buildTask('       >         project           >         hello        ', {
        }, true))
        'assert'('hello', 'project')(task.buildTask('>project>hello', {}, true))
    }

    @Test
    public void shouldBuildTaskForProject() {
        'assert'("install ${task.mavenProperties}", 'project')(task.buildTask('project', {}, true))
        'assert'("install ${task.mavenProperties}", 'project')(task.buildTask('  project  ', {}, true))
    }

    private static Closure 'assert'(String command, name) {
        return { buildTask ->
            assertEquals(command, buildTask.commandToExecute)
            assertEquals(name, buildTask.name)
        }
    }
}
