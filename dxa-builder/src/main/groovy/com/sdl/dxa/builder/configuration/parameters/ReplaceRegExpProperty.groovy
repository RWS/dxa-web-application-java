package com.sdl.dxa.builder.configuration.parameters

class ReplaceRegExpProperty extends Property {

    String regexp

    def thatReplaces(String leftRegExp, String valueRegExp, String rightRegExp) {
        def normalize = { it.replaceAll("([^\\\\])\\(", '$1').replaceAll("([^\\\\])\\)", '$1').replaceAll("^\\(", '') }

        this.regexp = "(${normalize(leftRegExp)})(${normalize(valueRegExp)})(${normalize(rightRegExp)})"
        this
    }

    @Override
    def processProperty(String filename, String value) {
        if (value == null) {
            return
        }

        def file = new File(filename)
        def replacement = file.text.replaceAll(this.regexp, '$1' + value + '$3')
        file.write(replacement)
        println "Replaced in file '$filename' by regexp '$regexp', set value '$value'"
    }
}
