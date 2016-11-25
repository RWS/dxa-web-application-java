package com.sdl.dxa.builder.utils.configuration

import org.junit.Assert
import org.junit.Test

class XmlWriterBuilderTest {

    @Test
    public void shouldInstantiateXmlBuilder() {
        def xmlWriter = new XmlWriterBuilder().setup {
            from "from-file"
            to "to-file"
        }

        Assert.assertEquals("from-file", xmlWriter.from.path);
        Assert.assertEquals("to-file", xmlWriter.to.path);
    }

    @Test
    public void shouldImplicitlyInitToField() {
        def xmlWriter = new XmlWriterBuilder().setup {
            from "from-file"
        }

        Assert.assertEquals("from-file", xmlWriter.from.path);
        Assert.assertEquals("from-file", xmlWriter.to.path);
    }

}
