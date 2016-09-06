package com.sdl.dxa.builder.maven

interface Defaults {
    static final String DEFAULT_COMMAND = 'install'
    static final int NUMBER_THREADS = Runtime.getRuntime().availableProcessors()
    static final String MAVEN_PROPERTIES = "-e"
    static final boolean IS_VERBOSE = false
    static final boolean IS_BATCH = false
}