package com.sdl.dxa.builder.utils.configuration

import org.junit.Assert
import org.junit.Test

class XmlWriterBuilderTest {

    @Test
    public void shouldInstantiateXmlBuilder() {
        def fromPath = path("test.xml")
        def pathTo = path("test_new.xml")

        def xmlWriter = new XmlWriterBuilder().setup {
            from fromPath
            to pathTo
        }

        Assert.assertEquals("test.xml", xmlWriter.from.name);
        Assert.assertEquals("test_new.xml", xmlWriter.to.name);
    }

    @Test
    public void shouldImplicitlyInitToField() {
        def fromPath = path("test.xml")
        def xmlWriter = new XmlWriterBuilder().setup {
            from fromPath
        }

        Assert.assertEquals("test.xml", xmlWriter.from.name);
        Assert.assertEquals("test.xml", xmlWriter.to.name);
    }

    def String path(String path) {
        new File(getClass().classLoader.getResource("xml").file, path).absolutePath
    }
}
