package com.sdl.dxa

import com.sdl.dxa.maven.MavenRunner
import org.gradle.api.Plugin
import org.gradle.api.Project

class DxaBuilder implements Plugin<Project> {
    @SuppressWarnings(["GroovyAssignabilityCheck"])
    void apply(Project project) {
        project.extensions.create("maven", MavenRunner.MavenExtension)

        project.task("maven.help") << MavenRunner.help(project.maven)
        project.task("maven.run") << MavenRunner.run(project.maven)
    }
}


