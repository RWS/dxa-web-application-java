package com.sdl.dxa.builder.configuration.parameters

class Parameter {
    String description
    Property[] properties
    Closure<String> dynamicDefault
    Validator validator
    String value

    String versionAdded

    private boolean valid = false

    //region Builder methods
    Parameter withDescription(String description) {
        this.description = description
        this
    }

    Parameter withProperties(Property... properties) {
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
        this.value = project && project.hasProperty(cliName) ? (project[cliName] as String).trim() : defaultValue
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

        batch ? get() : request()
    }

    String request() {
        println "${description}? <Enter> for default '${value ?: 'no default'}'"
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
            validator?.describe()
            throw new IllegalArgumentException("Invalid value '${value}' for '${description}'")
        }
        value
    }

    private boolean isValid(String userValue) {
        valid = valid || validator == null || validator.validate(userValue)
    }
}
