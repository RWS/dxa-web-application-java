package com.sdl.dxa.builder.configuration.parameters

/**
 * Property that is capable to work with {@code *.properties} files.
 */
class PropertiesFileProperty extends Property {
    @Override
    def processProperty(String filename, String value) {
        if (value == null) {
            return
        }

        def file = new File(filename)
        def properties = new Properties()
        properties.load(file.newDataInputStream())

        if (this.append) {
            def old = properties.get(this.name)
            if (old == null || !(old as String).contains(value)) {
                properties.setProperty(this.name, "${(old == null || old == '' ? '' : old + ', ')}${value}")
            }
        } else {
            properties.setProperty(this.name, value)
        }
        println "Modified $filename, set ${name == null ? 'property to' : (name + ' =')} $value"
        properties.store(file.newWriter(), 'UTF-8')
    }
}
