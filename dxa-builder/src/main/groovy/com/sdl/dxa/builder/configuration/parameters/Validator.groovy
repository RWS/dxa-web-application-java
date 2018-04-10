package com.sdl.dxa.builder.configuration.parameters

import org.apache.commons.validator.routines.DomainValidator
import org.apache.commons.validator.routines.InetAddressValidator
import org.apache.commons.validator.routines.UrlValidator

import static org.apache.commons.validator.routines.UrlValidator.*

class Validator {
    String description

    Closure<Boolean> validate

    boolean caseSensitive

    def describe = { String prefix = "" ->
        println prefix + description
    }

    //region Builder methods
    Validator withDescription(String description) {
        this.description = description
        this
    }

    Validator withValidateClosure(Closure<Boolean> closure) {
        this.validate = closure
        this
    }

    Validator setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive
        this
    }
//endregion

    //region Validators create methods
    static Validator valueInList(String... values) {
        def validator = new Validator(description: "Value should be one of ${values}", validate: {
            if (!delegate.caseSensitive) {
                it = it.toLowerCase()
                values = values*.toLowerCase()
            }
            values.contains(it)
        })
        validator.validate.delegate = validator
    }

    static Validator commaInputFromList(Collection<String> values) {
        def validator = new Validator(description: "Allowed values are: ${values}, or '-' for empty list", validate: { String input ->
            if ('-' == input) {
                return true
            }

            if (!delegate.getCaseSensitive()) {
                input = input.toLowerCase()
                values = values*.toLowerCase()
            }

            def tokenize = input.tokenize(' ,')
            values.containsAll(tokenize)
        })
        validator.validate.delegate = validator
    }

    static Validator notEmpty() {
        new Validator(description: "Should not be empty", validate: {
            it != null && it != '' && (!(it instanceof Collection) || (it as Collection).size() > 0)
        })
    }

    static Validator intNumber() {
        regexp('Should be an integer number', /\d+/)
    }

    static Validator regexp(String description, String regexp) {
        new Validator(description: "${description}", validate: { it?.matches(regexp) })
    }

    static Validator or(Validator... validators) {
        new Validator(description: 'Should match any of passed validators', validate: { String value ->
            validators.any { it.validate(value) }
        }, describe: { String prefix = "" ->
            println prefix + "Should be any of:"
            validators.each { it.describe(prefix + '    ') }
        })
    }

    static Validator and(Validator... validators) {
        new Validator(description: 'Should match all of passed validators', validate: { String value ->
            validators.every { it.validate(value) }
        }, describe: { String prefix = "" ->
            println prefix + "Should be all:"
            validators.each { it.describe(prefix + '    ') }
        })
    }

    static Validator domainName(boolean validWithPort = false) {
        new Validator(description: "Should match a valid Domain Name ${validWithPort ? "with" : "without"} port", validate: { String it ->
            def result = notEmpty().validate(it as String)

            result &= !it.contains('://')
            it = removePath(it)

            if (validWithPort) {
                if (it.contains(':')) {
                    def arr = it.split(':')
                    it = arr[0]
                    result &= arr.length == 2 && intNumber().validate(arr[1])
                }
            }
            result && DomainValidator.getInstance(true).isValid(it as String)
        })
    }

    static Validator ip() {
        new Validator(description: 'Should match a valid IP address', validate: { String it ->
            it = removePath(it)
            notEmpty().validate(it as String) && new InetAddressValidator().isValid(it as String)
        })
    }

    static Validator url() {
        new Validator(description: 'Should match a valid URL', validate: { String url ->
            url = url.replace("sdl.corp", "sdl.com") // workaround for valid internal corporate domain
            notEmpty().validate(url) &&
                    (new UrlValidator(ALLOW_ALL_SCHEMES + ALLOW_LOCAL_URLS + ALLOW_2_SLASHES).isValid(url)
                            || url?.matches(/^((https?|ftp):\/\/)?localhost(:\d+)?(\/[^\s]*)?$/))
        })
    }

    static Validator emptyValue() {
        new Validator(description: 'Should be empty value or null', validate: { String it ->
            it == null || it.isEmpty()
        })
    }
    //endregion

    private static String removePath(String ipOrDomain) {
        ipOrDomain == null ? '' : ipOrDomain.replaceFirst("[\\\\/].*", '')
    }
}