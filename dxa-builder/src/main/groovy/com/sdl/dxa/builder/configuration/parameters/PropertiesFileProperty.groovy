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

        if (this.append) {
            def list = config.getList(this.name)
            if (!list.contains(value)) {
                list.add(value)
                config.setProperty(this.name, list)
            }
        } else {
            config.setProperty(this.name, value)
        }
        println "Modified $filename, set ${name == null ? 'property to' : (name + ' =')} $value"
        layout.save(new FileWriter(filename, false))
    }
}
