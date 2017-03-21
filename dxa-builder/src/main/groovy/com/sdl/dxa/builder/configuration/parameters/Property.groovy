package com.sdl.dxa.builder.configuration.parameters

class Property {
    List<String> files
    String name
    boolean append
    Map<String, String> valueMapping

    //region Builder methods
    Property withMapping(String mapping) {
        valueMapping = [:]
        def split = mapping.split(';')
        split.each {
            String[] ab = it.split('->')
            ab[0].split(',').each {
                def key = it.trim()
                def value = key == '$self$' ? key : ab[1].trim()
                if (value == '$null$') {
                    value = null
                } // mapping to null value
                valueMapping.put key, value
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

    Property inFile(String file) {
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
    //endregion

    String deriveValue(String value) {
        if (value == null) {
            return null
        }
        if (this.valueMapping) {
            if (this.valueMapping.containsKey(value)) {
                return this.valueMapping.get(value)
            } else if (this.valueMapping.containsKey('$self$')) {
                return value
            }
            throw new IllegalArgumentException("Cannot map $value for property $name")
        } else {
            return value
        }
    }
}
