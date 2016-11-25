package com.sdl.dxa.builder.configuration.parameters

class Parameter {
    String name
    String placeholder
    Property[] properties
    Closure<String> dynamicDefault
    Validator validator
    String value

    private boolean valid = false

    //region Builder methods
    Parameter withName(String name) {
        this.name = name
        this
    }

    Parameter withPlaceholder(String placeholder) {
        this.placeholder = placeholder
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
    //endregion

    def request() {
        println "Enter the value for ${name?.toUpperCase()}? <Enter> for default '${value ?: 'no default'}'"
        validator?.describe()

        def userValue
        if (System.console()) {
            userValue = System.console().readLine("> ${name}: ") ?: value
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

    def get() {
        if (!isValid(value)) {
            validator?.describe()
            throw new IllegalArgumentException("Invalid value '${value}' for '${name}'")
        }
        value
    }

    private boolean isValid(String userValue) {
        valid = valid || validator == null || validator.validate(userValue)
    }
}
