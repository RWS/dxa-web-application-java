package com.sdl.dxa.maven

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MavenRunner {
    static class MavenExtension {
        def String defaultCommand = "install"
        def String command
        def List<String[]> projects
        def numberThreads = Runtime.getRuntime().availableProcessors()
        def mavenProperties = "-T 1C -e"
    }

    private MavenRunner() {
    }

    static def Closure run(MavenExtension extension) {
        return {
            def pool = Executors.newFixedThreadPool(extension.numberThreads)
            def commandToExecute = getCommandToExecute(extension)

            println "Building ${extension.projects}"
            def mvnVersion = BuildTask.determineShell() + "mvn --version"
            println mvnVersion
            mvnVersion.execute().in.eachLine { println it }
            println ""

            extension.projects.each { arr ->
                def tasks = []
                def CountDownLatch latch

                arr.each { task ->
                    task = task.trim()

                    def taskName = task, command = commandToExecute
                    if (task.startsWith(">")) {
                        def split = task.substring(1).split(">")
                        taskName = split[0].trim()
                        command = split[1].trim()
                    }

                    tasks << new BuildTask(taskName, command, { output ->
                        if (output.code != 0) {
                            pool.shutdown()

                            println "= FAILED (in ${output.timeSeconds}s): "
                            output.lines.each { println it }
                            println "Well, there is an error. Press <Enter> to finish."
                            System.in.read()
                            System.exit(-1)
                        } else {
                            println "= SUCCESS (in ${output.timeSeconds}s): ${output.command}"
                        }
                        latch.countDown()
                    })
                }
                latch = new CountDownLatch(tasks.size())
                println "Waiting for completion of ${arr}"
                pool.invokeAll(tasks)
                latch.await(42, TimeUnit.MINUTES)
            }
        }
    }

    static def Closure help(MavenExtension extension) {
        return {
            println """
    Usage:
        gradlew(.bat) build -Pcommand="<command>"

    <command> is passed to Maven as it is. If <command> is missed then the default command is used which is: ${
                extension.defaultCommand
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
    maven {
        String defaultCommand = "install",
        String command,
        List<String[]> projects,
        int numberThreads = <number of processors>
    }
    Example:
    maven.projects = [
        //syntax: name of project = runs command on projects in parallel
        //first run: set of self-independent project
        ["dxa-project1", "dxa-project2", "dxa-project3"],

        //syntax: "> NAME > COMMAND" = runs COMMAND on NAME
        //second run: runs 'dependencies:tree -Pweb8' goal on 'dxa-project1'
        ["> dxa-project1 > dependencies:tree -Pweb8"],

        //syntax: mixed
        //third run: runs command on dxa-project1 AND 'dependencies:tree -Pweb8' goal on 'dxa-project2' in parallel
        ["dxa-project1", "> dxa-project2 > dependencies:tree -Pweb8"],
    ]
    maven.defaultCommand = "clean install" //optional
    maven.command = "clean package" //optional, passes custom command to Maven
    maven.numberThreads = 1 //optional

    """
        }
    }

    static String getCommandToExecute(MavenExtension extension) {
        return (extension.command ?: extension.defaultCommand) + " ${extension.mavenProperties}"
    }
}
