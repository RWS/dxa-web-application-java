package com.sdl.dxa.builder.maven

import org.gradle.internal.os.OperatingSystem

import java.util.concurrent.Callable

class BuildTask implements Callable<Output> {
    String name, commandToExecute
    Closure callback
    boolean verbose

    BuildTask(String commandToExecute, Closure callback, boolean verbose) {
        this.commandToExecute = commandToExecute
        this.callback = callback
        this.verbose = verbose
    }

    BuildTask(String name, String commandToExecute, Closure callback, boolean verbose) {
        this.name = name
        this.commandToExecute = commandToExecute
        this.callback = callback
        this.verbose = verbose
    }

    @Override
    Output call() throws Exception {
        runMaven(commandToExecute, name == null || name.empty ? null : findPath([name, "pom.xml"]))
    }

    @SuppressWarnings(["GroovyAssignabilityCheck"])
    String findPath(List<String> parts) {
        String joined = new File('.').absolutePath + File.separator + parts.join(File.separator)

        if (parts.size() == 1 || new File(joined).exists()) {
            return joined
        }
        int len = parts.size() - 1
        findPath(parts.getAt(1..len))
    }

    Output runMaven(String command, String pomPath) {
        def start = System.currentTimeSeconds()
        def toRun = "${determineShell()} mvn ${command}"
        if (pomPath != null) {
            toRun += " -f \"${pomPath}\""
        }

        def output = new Output(command: toRun, lines: [], code: 0)

        println "Running in background ${toRun}"

        def execute = toRun.execute()
        execute.in.eachLine {
            if (verbose) {
                println it
            } else {
                output.lines << it
            }
        }
        execute.waitFor()
        output.code = execute.exitValue()
        output.timeSeconds = System.currentTimeSeconds() - start
        callback(output)
    }

    static String determineShell() {
        OperatingSystem.current().windows ? "cmd /c " : ""
    }
}

class Output {
    List<String> lines
    int code
    String command
    int timeSeconds
}
