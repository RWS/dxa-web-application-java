package com.sdl.dxa.builder.configuration.writers

import com.sdl.dxa.builder.configuration.parameters.XmlProperty
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.filter.Filters
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import org.jdom2.xpath.XPathFactory

class XmlWriter {
    private xPathFactory = XPathFactory.instance()
    private Document document

    protected Format xmlFormat = Format.getPrettyFormat().setIndent("    ")
    protected File from, to

    XmlWriter format(String format) {
        if (format == 'raw') {
            this.xmlFormat = Format.getRawFormat()
        }
        this
    }

    XmlWriter format(Format format) {
        this.xmlFormat = format
        this
    }

    XmlWriter from(String fileName) {
        this.from = new File(fileName)
        this
    }

    XmlWriter to(String fileName) {
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

    XmlWriter addAttributes(Map<String, Tuple2<String, String>> mapToAdd, XmlProperty xmlProperty) {
        bulkEdit mapToAdd, xmlProperty, { nodes, Tuple2<String, String> value ->
            nodes*.setAttribute(value.first, value.second)
        }
        this
    }

    XmlWriter addNodesAfter(Map<String, Tuple2<String, String>> mapToAdd, XmlProperty xmlProperty) {
        bulkEdit mapToAdd, xmlProperty, { nodes, Tuple2<String, String> value ->
            def tagName = value.first
            def tagValue = value.second

            nodes*.each {
                def content = (it as Element).getParent().getContent()
                content.add(content.indexOf(it) + 1, new Element(tagName).setText(tagValue))
            }
        }
        this
    }

    XmlWriter modifyByXPath(Map<String, String> mapToModify, XmlProperty xmlProperty) {
        bulkEdit mapToModify, xmlProperty, { nodes, value ->
            nodes*.value = value
        }
        this
    }


    XmlWriter addAttributes(Map<String, Tuple2<String, String>> mapToAdd) {
        addAttributes mapToAdd, null
    }

    XmlWriter addNodesAfter(Map<String, Tuple2<String, String>> mapToAdd) {
        addNodesAfter mapToAdd, null
    }

    XmlWriter modifyByXPath(Map<String, String> mapToModify) {
        modifyByXPath mapToModify, null
    }

    XmlWriter modify(XmlProperty xmlProperty) {
        addNodesAfter(xmlProperty.addNodesMap, xmlProperty)
        addAttributes(xmlProperty.addAttributesMap, xmlProperty)
        modifyByXPath(xmlProperty.modifyMap, xmlProperty)
    }

    private bulkEdit(Map<String, ?> map, XmlProperty xmlProperty, Closure callback) {
        map?.each {
            if (xmlProperty != null && xmlProperty.namespace != null) {
                return callback(xPathFactory.compile(it.key, Filters.fpassthrough(), null,
                        xmlProperty.namespace).evaluate(document), it.value)
            } else {
                return callback(xPathFactory.compile(it.key).evaluate(document), it.value)
            }

        }
    }
}