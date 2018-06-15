package com.sdl.dxa.builder.maven

import org.gradle.internal.os.OperatingSystem

import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

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
        def toRun = "${determineShell()} ${command}"
        if (pomPath != null) {
            toRun += " -f \"${pomPath}\""
        }

        def output = new Output(command: toRun, lines: [], code: 0)

        println "Running in background (timeout 10 minutes): ${toRun}"

        def execute = null
        try {
            execute = toRun.execute()
            def out = new StringBuffer(), err = new StringBuffer()
            execute.consumeProcessOutput(out, err)
            execute.waitFor(10, TimeUnit.MINUTES)
            if (verbose) {
                println "$toRun:: OUT: $out --eof;"
                println "$toRun:: ERR: $err --eof;"
            } else {
                output.lines.addAll(out.readLines())
                output.lines.addAll(err.readLines())
            }
            output.code = execute.exitValue()
            output
        } catch (Exception e) {
            output.lines = Collections.singletonList(e.message)
            output.code = execute == null ? -255 : execute.exitValue()
            output
        } finally {
            output.timeSeconds = System.currentTimeSeconds() - start
            callback(output)
        }
    }

    static String determineShell() {
        OperatingSystem.current().windows ? "cmd /c" : ""
    }
}

class Output {
    List<String> lines
    int code
    String command
    long timeSeconds
}
