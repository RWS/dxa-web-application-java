package com.sdl.dxa.builder.utils.configuration

import org.jdom2.output.Format
import org.junit.Assert
import org.junit.Test

class XmlWriterTest {

    @Test
    public void shouldWriteTheProperty() {
        def fromPath = path("test.xml")
        def pathTo = path("test_new.xml")
        def example = path("test_reformatted.xml")

        def writer = new XmlWriterBuilder().setup {
            from fromPath
            to pathTo
        }

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

        Assert.assertEquals(new File(example).text, new File(pathTo).text);
    }

    @Test
    public void shouldPreserveRawFormatIfSet() {
        def fromPath = path("test.xml")
        def pathTo = path("test_new.xml")
        def example = path("test.xml")

        def writer = new XmlWriterBuilder().setup {
            from fromPath
            to pathTo
            format "raw"
        }.addAttributes([:])
                .addNodesAfter([:])
                .modifyByXPath([:])
                .save()

        Assert.assertEquals(new File(example).text, new File(pathTo).text);

        writer = new XmlWriterBuilder().setup {
            from fromPath
            to pathTo
            format Format.getRawFormat()
        }.addAttributes([:])
                .addNodesAfter([:])
                .modifyByXPath([:])
                .save()

        Assert.assertEquals(new File(example).text, new File(pathTo).text);
    }

    def String path(String path) {
        new File(getClass().classLoader.getResource("xml").file, path).absolutePath
    }
}