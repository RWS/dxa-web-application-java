package com.sdl.dxa.builder.configuration.parameters

class Property {
    String[] files
    String name
    boolean append
    String placeholder
    Map<String, String> valueMapping

    //region Builder methods
    Property withMapping(String mapping) {
        valueMapping = [:]
        def split = mapping.split(';')
        split.each {
            String[] ab = it.split('->')
            ab[0].split(',').each {
                valueMapping.put it.trim(), ab[1].trim()
            }
        }
        this
    }

    Property shouldAppend() {
        this.append = true
        this
    }

    Property shouldReplace() {
        this.append = false
        this
    }

    Property withFile(String file) {
        if (!this.files) {
            this.files = []
        }
        this.files << file
        this
    }

    Property withName(String name) {
        this.name = name
        this
    }

    Property withPlaceholder(String placeholder) {
        this.placeholder = placeholder
        this
    }
    //endregion

}
