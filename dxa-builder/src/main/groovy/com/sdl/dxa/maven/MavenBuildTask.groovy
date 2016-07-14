package com.sdl.dxa.maven

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Maven build task accepts a list of tasks that should be executed with Maven, and executes them.
 */
@Slf4j
class MavenBuildTask extends DefaultTask {
    private static boolean isVersionShown = false

    String command
    List<String[]> configurations
    String defaultCommand = Defaults.DEFAULT_COMMAND
    int numberThreads = Defaults.NUMBER_THREADS
    String mavenProperties = Defaults.MAVEN_PROPERTIES

    def customCommandDelimiter = /\s*>\s*/

    static {
        if (!isVersionShown) {
            printMvnVersion()
            isVersionShown = true
        }
    }

    @TaskAction
    def run() {
        def pool = Executors.newFixedThreadPool(numberThreads)

        println "Building ${configurations}"

        CountDownLatch latch

        def callback = { output ->
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
        }

        configurations.each { tasks ->
            def parallelTasks = []

            tasks.each { task ->
                parallelTasks << buildTask(task, callback)
            }

            latch = new CountDownLatch(tasks.size())
            println "Waiting for completion of ${tasks}"
            pool.invokeAll(parallelTasks)
            latch.await(42, TimeUnit.MINUTES)
        }

    }

    def BuildTask buildTask(String task, Closure callback) {
        task = task.trim()

        if (task =~ customCommandDelimiter) {
            log.debug('Found the custom command, processing')
            def parts = task.split(customCommandDelimiter) as List
            parts.remove(0)

            if (parts.size() == 2) {
                def command = parts[1].trim()
                def taskName = parts[0].trim()
                log.debug('The custom command {} is to be executed on {}', command, taskName)
                return new BuildTask(taskName, command, callback)
            } else if (parts.size() == 1) {
                def command = parts[1].trim()
                log.debug('The custom command {} is to be executed on current path', command)
                return new BuildTask(command, callback)
            } else {
                throw new IllegalArgumentException("Unsupported configuration")
            }
        }

        new BuildTask(task, getCommandToExecute(), callback)
    }

    private String getCommandToExecute() {
        return (command ?: defaultCommand) + " ${mavenProperties}"
    }

    private static void printMvnVersion() {
        def mvnVersion = BuildTask.determineShell() + "mvn --version"
        println mvnVersion
        mvnVersion.execute().in.eachLine { println it }
        println ""
    }
}
