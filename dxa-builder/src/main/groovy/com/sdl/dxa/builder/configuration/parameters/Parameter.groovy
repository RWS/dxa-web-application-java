package com.sdl.dxa.builder.configuration.parameters

class Parameter {
    String description
    Property[] properties
    Closure<String> dynamicDefault
    Validator validator
    String value
    String cliName

    // We don't care about case sensitivity in most cases
    boolean isValueCaseSensitive

    String versionAdded

    private boolean valid = false

    //region Builder methods
    Parameter withDescription(String description) {
        this.description = description
        this
    }

    Parameter withProperties(Property... properties) {
        properties.each { it.displayName = this.cliName }
        this.properties = properties
        this
    }

    Parameter withDynamicDefault(Closure<String> closure) {
        this.dynamicDefault = closure
        this
    }

    Parameter withValidator(Validator validator) {
        this.validator = validator
        this
    }

    Parameter withDefaultValue(Object project, String cliName, String defaultValue) {
        this.cliName = cliName
        this.value = project && project.hasProperty(cliName) ? (project[cliName] as String).trim() : defaultValue
        this
    }

    Parameter withCaseSensitiveValue() {
        this.isValueCaseSensitive = true
        this
    }

    Parameter withSystemEnv(String varName) {
        if (System.getenv(varName) != null) {
            this.value = System.getenv(varName)
        }
        this
    }

    Parameter versionAdded(String version) {
        this.versionAdded = version
        this
    }
    //endregion

    boolean isSupportedInCurrentVersion(String currentVersion) {
        if (!currentVersion || !versionAdded) {
            return true
        }
        def normalize = { String version ->
            Integer.parseInt(version.replaceAll(/[^\d]/, "").padRight(5, "0").substring(0, 5))
        }
        normalize(currentVersion) >= normalize(versionAdded)
    }

    String process(boolean batch, Map<String, ?> configuration = [:]) {
        if (!this.value && this.dynamicDefault) {
            this.value = this.dynamicDefault(configuration)
        }

        def temp = batch ? get() : request()
        this.value = this.isValueCaseSensitive ? temp : temp.toLowerCase()

        this.value
    }

    String request(Map<Property, String> props, String version, boolean isBatch, Map<String, ?> configuration = [:]) {
        if (!this.isSupportedInCurrentVersion(version)) {
            return null
        }

        println("::: ${this.description}\n" +
                "::: ${this.process(isBatch, configuration)}")
        println ''

        if (this.properties) {
            def param = this
            this.properties.each {
                it.caseSensitive = param.isValueCaseSensitive
                props[it] = this.get()
            }
        }

        return this.get()
    }

    String request() {
        println "\n  ::  ${description}\n  ::  Default value: '${value ?: 'no default'}'" +
                (this.isValueCaseSensitive ? "\n  ::  This value is case-sensitive" : '') +
                "\n\nPress <Enter> to choose default value or type 'halt' to stop"

        validator?.describe()

        def userValue
        if (System.console()) { //special treatment for Windows's cmd.exe
            userValue = System.console().readLine("> ${description}: ") ?: value
        } else {
            userValue = System.in.newReader().readLine() ?: value
        }

        userValue = userValue == null ? '' : userValue.trim()

        if (userValue.toLowerCase() == 'halt') {
            System.exit(0)
        }

        if (!isValid(userValue)) {
            return request()
        }
        value = userValue

        value
    }

    String get() {
        if (!isValid(value)) {
            if (this.isValueCaseSensitive) {
                println "This value is case-sensitive"
            }
            validator?.describe()
            throw new IllegalArgumentException("Invalid value '${value}' for '${description}'")
        }
        value
    }

    private boolean isValid(String userValue) {
        valid = valid || validator == null || validator.setCaseSensitive(this.isValueCaseSensitive).validate(userValue)
    }
}
