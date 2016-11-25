package com.sdl.dxa.builder.utils.configuration

import com.sdl.dxa.builder.utils.GenericBuilder
import org.jdom2.output.Format

class XmlWriterBuilder extends GenericBuilder<ConfigurationWriter.XmlWriter> {

    @Override
    ConfigurationWriter.XmlWriter init() {
        return new ConfigurationWriter.XmlWriter()
    }

    @Override
    ConfigurationWriter.XmlWriter finish() {
        if (instance.to == null) {
            instance.to = instance.from
        }

        instance.open()
    }

    def format(String format) {
        if (format == 'raw') {
            instance.xmlFormat = Format.getRawFormat()
        }
    }

    def format(Format format) {
        instance.xmlFormat = format
    }

    @Override
    Closure setter(String methodName) {
        if (methodName in ['to', 'from']) {
            return { Object[] args ->
                instance."$methodName" = new File((String) args[0])
            }
        }

        return super.setter(methodName)
    }
}
