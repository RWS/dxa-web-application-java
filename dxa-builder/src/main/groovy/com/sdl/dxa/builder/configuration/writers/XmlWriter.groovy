package com.sdl.dxa.builder.configuration.writers

import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import org.jdom2.xpath.XPathFactory

class XmlWriter {
    private xPathFactory = XPathFactory.instance()
    private Document document

    protected Format xmlFormat = Format.getPrettyFormat().setIndent("    ")
    protected File from, to

    def format(String format) {
        if (format == 'raw') {
            this.xmlFormat = Format.getRawFormat()
        }
        this
    }

    def format(Format format) {
        this.xmlFormat = format
        this
    }

    def from(String fileName) {
        this.from = new File(fileName)
        this
    }

    def to(String fileName) {
        this.to = new File(fileName)
        this
    }

    XmlWriter open() {
        if (this.to == null) {
            this.to = this.from
        }
        document = new SAXBuilder().build(new FileReader(from))
        this
    }

    XmlWriter save() {
        new XMLOutputter().with {
            format = xmlFormat
            output(document, to.newPrintWriter())
        }
        this
    }

    XmlWriter addAttributes(Map<String, Tuple2<String, String>> mapToAdd) {
        bulkEdit mapToAdd, { nodes, Tuple2<String, String> value ->
            nodes*.setAttribute(value.first, value.second)
        }
        this
    }

    XmlWriter addNodesAfter(Map<String, Tuple2<String, String>> mapToAdd) {
        bulkEdit mapToAdd, { nodes, Tuple2<String, String> value ->
            def tagName = value.first
            def tagValue = value.second

            nodes*.each {
                def content = (it as Element).getParent().getContent()
                content.add(content.indexOf(it) + 1, new Element(tagName).setText(tagValue))
            }
        }
        this
    }

    XmlWriter modifyByXPath(Map<String, String> mapToModify) {
        bulkEdit mapToModify, { nodes, value ->
            nodes*.value = value
        }
        this
    }

    private bulkEdit(Map<String, ?> map, Closure callback) {
        map?.each {
            callback xPathFactory.compile(it.key).evaluate(document), it.value
        }
    }
}