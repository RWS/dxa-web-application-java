package com.sdl.dxa.builder.utils

abstract class GenericBuilder<T> {
    T instance

    def abstract T init()

    def abstract T finish()

    def Closure setter(String methodName) {
        null
    }

    def T setup(Closure definition) {
        instance = init()
        runClosure definition
        finish()
    }

    def methodMissing(String methodName, args) {
        Object[] arguments = args
        def setter = setter(methodName)
        if (setter) {
            runClosure setter, arguments
        }
    }

    def runClosure(Closure runClosure, Object... args) {
        Closure runClone = runClosure.clone() as Closure
        runClone.delegate = this
        runClone.resolveStrategy = Closure.DELEGATE_ONLY
        runClone(args)
    }
}
