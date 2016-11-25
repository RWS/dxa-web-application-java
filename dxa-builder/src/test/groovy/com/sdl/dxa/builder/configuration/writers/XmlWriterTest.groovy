package com.sdl.dxa.builder.configuration.writers

import org.jdom2.output.Format
import org.junit.Assert
import org.junit.Test

class XmlWriterTest {

    @Test
    void shouldWriteTheProperty() {
        def fromPath = path("test.xml")
        def pathTo = path("test_new.xml")
        def example = path("test_reformatted.xml")

        def writer = new XmlWriter()
                .from(fromPath)
                .to(pathTo)
                .open()

        writer.addAttributes([
                '/element/outerTag/innerTag': new Tuple2<>("property", "new value")
        ]).addNodesAfter([
                '/element/parent/following-sibling::sibling': new Tuple2<>("newTag", "new value")
        ]).modifyByXPath([
                '/element/@property'                                  : 'new value',
                '/element/parent/text()'                              : 'new value',
                '/element/parent/@property'                           : 'new value',
                '/element/parent/following-sibling::sibling/text()[1]': 'new value',
                '/element/outerTag/innerTag/text()'                   : 'new value',
        ]).save()

        Assert.assertEquals(new File(example).text, new File(pathTo).text)
    }

    @Test
    void shouldPreserveRawFormatIfSet() {
        def fromPath = path("test.xml")
        def pathTo = path("test_new.xml")
        def example = path("test.xml")

        def writer = new XmlWriter()
                .from(fromPath)
                .to(pathTo)
                .format("raw")
                .open()

        writer.addAttributes([:])
                .addNodesAfter([:])
                .modifyByXPath([:])
                .save()

        Assert.assertEquals(new File(example).text, new File(pathTo).text)

        writer = new XmlWriter()
                .from(fromPath)
                .to(pathTo)
                .format(Format.getRawFormat())
                .open()

        writer.addAttributes([:])
                .addNodesAfter([:])
                .modifyByXPath([:])
                .save()

        Assert.assertEquals(new File(example).text, new File(pathTo).text)
    }

    String path(String path) {
        new File(getClass().classLoader.getResource("xml").file, path).absolutePath
    }
}