package com.sdl.dxa.builder.configuration.parameters

import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.commons.configuration.PropertiesConfigurationLayout

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

        PropertiesConfiguration config = new PropertiesConfiguration()
        PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout(config)

        layout.load(new InputStreamReader(new FileInputStream(file)))

        def properties = new Properties()
        properties.load(file.newDataInputStream())

        if (this.append) {
            def old = properties.get(this.name)
            if (old == null || !(old as String).contains(value)) {
                config.setProperty(this.name, "${(old == null || old == '' ? '' : old + ', ')}${value}")
            }
        } else {
            config.setProperty(this.name, value)
        }
        println "Modified $filename, set ${name == null ? 'property to' : (name + ' =')} $value"
        layout.save(new FileWriter(filename, false))
    }
}
