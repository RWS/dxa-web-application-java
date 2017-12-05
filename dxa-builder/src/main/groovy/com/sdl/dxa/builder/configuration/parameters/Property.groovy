package com.sdl.dxa.builder.configuration.parameters

abstract class Property {
    List<String> files
    String name
    boolean append
    boolean caseSensitive
    Map<String, String> valueMapping

    Tuple2<String, String> wrap

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

    Property wrappedWith(String left, String right) {
        this.wrap = new Tuple2<>(left, right)
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

        def result

        if (this.valueMapping) {
            if (!this.caseSensitive) {
                value = value.toLowerCase()
            }

            if (this.valueMapping.containsKey(value)) {
                result = this.valueMapping.get(value)
            } else if (this.valueMapping.containsKey('$self$')) {
                // mapping when you have a special value or original value otherwise (no -> $null$; $self$)
                result = value
            } else {
                throw new IllegalArgumentException("Cannot map $value for property $name")
            }
        } else {
            result = value
        }

        return this.wrap ? (this.wrap.first + result + this.wrap.second) : result
    }

    abstract def processProperty(String filename, String value)
}
