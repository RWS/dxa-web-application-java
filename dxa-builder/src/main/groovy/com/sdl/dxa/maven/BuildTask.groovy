package com.sdl.dxa.maven

import org.gradle.internal.os.OperatingSystem

import java.util.concurrent.Callable

class BuildTask implements Callable<Output> {
    String name, commandToExecute
    Closure callback

    BuildTask(String name, String commandToExecute, Closure callback) {
        this.name = name
        this.commandToExecute = commandToExecute
        this.callback = callback
    }

    @Override
    Output call() throws Exception {
        runMaven(commandToExecute, findPath([name, "pom.xml"]))
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
        String toRun = "${determineShell()} mvn ${command} -f \"${pomPath}\""

        def output = new Output(command: toRun, lines: [], code: 0)

        println "Running in background ${toRun}"

        def execute = toRun.execute()
        execute.in.eachLine {
            output.lines << it
        }
        output.code = execute.exitValue()
        output.timeSeconds = System.currentTimeSeconds() - start
        callback(output)
    }

    static String determineShell() {
        OperatingSystem.current().windows ? "cmd /c " : "";
    }
}

class Output {
    List<String> lines
    int code
    String command
    int timeSeconds
}
