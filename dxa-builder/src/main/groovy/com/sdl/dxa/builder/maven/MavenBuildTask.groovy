package com.sdl.dxa.builder.maven

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
    List<List<String>> configurations
    String defaultCommand = Defaults.DEFAULT_COMMAND
    int numberThreads = Defaults.NUMBER_THREADS
    String mavenProperties = Defaults.MAVEN_PROPERTIES
    boolean verbose = Defaults.IS_VERBOSE
    boolean batch = Defaults.IS_BATCH

    def customCommandDelimiter = /\s*>\s*/

    static String mvnVersion() {
        def mvnVersion = BuildTask.determineShell() + "mvn --version"
        return mvnVersion.execute().text
    }

    static boolean isMvnInstalled() {
        try {
            return mvnVersion().contains("Maven")
        } catch (Exception ignored) {
            return false
        }
    }

    @TaskAction
    def run() {
        if (!configurations) {
            println "Nothing to do with Maven"
            return
        }

        printMvnVersion()

        def pool = Executors.newFixedThreadPool(numberThreads)
        def outputPool = Executors.newSingleThreadExecutor()

        println "Building ${configurations}"

        CountDownLatch latch

        def callback = { Output output ->
            if (output.code != 0) {
                pool.shutdown()
                outputPool.shutdown()

                output.lines.each { println it }

                println "= FAILED (in ${output.timeSeconds}s): "
                if (!batch) {
                    println "Well, there is an error. Press <Enter> to finish."
                    System.in.read()
                }
                println "Error building ${output.command}"
                System.exit(output.code)
            } else {
                outputPool.submit {
                    println "= SUCCESS (in ${output.timeSeconds}s): ${output.command}"
                    if (verbose) {
                        output.lines.each { println it }
                    }
                    println ""
                }
            }
            latch.countDown()
        }

        configurations.each { tasks ->
            def parallelTasks = []

            tasks.each { task ->
                parallelTasks << buildTask(task, callback, tasks.size() == 1 && verbose)
            }

            latch = new CountDownLatch(tasks.size())
            println "Waiting for completion of ${tasks}"
            pool.invokeAll(parallelTasks)
            latch.await(42, TimeUnit.MINUTES)
        }

    }

    BuildTask buildTask(String task, Closure callback, boolean verbose) {
        task = task.trim()

        if (task =~ customCommandDelimiter) {
            log.debug('Found the custom command, processing')
            def parts = task.split(customCommandDelimiter) as List
            parts.remove(0)

            if (parts.size() == 2) {
                def command = wrapCommand(parts[1].trim())
                def taskName = parts[0].trim()
                log.debug('The custom command {} is to be executed on {}', command, taskName)
                return new BuildTask(taskName, command, callback, verbose)
            } else if (parts.size() == 1) {
                def command = wrapCommand(parts[1].trim())
                log.debug('The custom command {} is to be executed on current path', command)
                return new BuildTask(command, callback, verbose)
            } else {
                throw new IllegalArgumentException("Unsupported configuration")
            }
        }

        new BuildTask(task, getCommandToExecute(), callback, verbose)
    }

    private String getCommandToExecute() {
        return wrapCommand(command ?: defaultCommand)
    }

    private String wrapCommand(String command) {
        return "${command} ${mavenProperties}"
    }

    private static void printMvnVersion() {
        if (!isVersionShown && isMvnInstalled()) {
            isVersionShown = true

            println mvnVersion()
        }
    }
}
