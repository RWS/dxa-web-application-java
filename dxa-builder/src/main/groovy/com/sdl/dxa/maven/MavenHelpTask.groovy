package com.sdl.dxa.maven

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MavenHelpTask extends DefaultTask {
    @SuppressWarnings("GrMethodMayBeStatic")
    @TaskAction
    def run() {
        println """
    Usage:
        gradlew(.bat) build -Pcommand="<command>"

    <command> is passed to Maven as it is. If <command> is missed then the default command is used which is: ${
            Defaults.DEFAULT_COMMAND
        }

    Example 1:
        gradlew(.bat) build -Pcommand="clean package -Pweb8"
    will run
        mvn -f project-name\\pom.xml clean package -Pweb8

    Example 2:
        gradlew(.bat) build
    will run
        mvn -f project-name\\pom.xml clean install

    Example 3:
        gradlew(.bat) build -Pcommand="-Pweb8"
    will run
        mvn -f project-name\\pom.xml -Pweb8
    which has actually no sense

    ======
    Syntax of task definition (extension):
    task myTask(type: MavenBuildTask) {
        String defaultCommand = "install",
        List<List<String>> configurations,
        int numberThreads = <number of processors>
        String mavenProperties
        boolean verbose
    }
    Example:
    task myTask(type: MavenBuildTask) {
        configurations = [
            //syntax: name of project = runs command on projects in parallel
            //first run: set of self-independent project
            ["dxa-project1", "dxa-project2", "dxa-project3"],

            //syntax: "> NAME > COMMAND" = runs COMMAND on NAME
            //second run: runs 'mvn dependencies:tree -Pweb8' goal on 'dxa-project1'
            ["> dxa-project1 > dependencies:tree -Pweb8"],

            //syntax: "> > COMMAND" = runs COMMAND, NAME is empty
            //third run: runs 'mvn -version'
            ["> > -version"],

            //syntax: mixed
            //fourth run: runs command on dxa-project1 AND 'mvn dependencies:tree -Pweb8' goal on 'dxa-project2' in parallel
            ["dxa-project1", "> dxa-project2 > dependencies:tree -Pweb8"]
        ]

        defaultCommand = "clean install" //optional
        numberThreads = 1 //optional
        verbose = true
        mavenProperties = "--debug"
    }


    """
    }
}
