package com.sdl.dxa.builder.utils.configuration

import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import org.jdom2.xpath.XPathFactory

final class ConfigurationWriter {

    final static class XmlWriter {
        def private xPathFactory = XPathFactory.instance()
        def private Document document

        def Format xmlFormat = Format.getPrettyFormat().setIndent("    ")
        File from, to


        def protected XmlWriter open() {
            document = new SAXBuilder().build(new FileReader(from))
            this
        }

        def XmlWriter save() {
            new XMLOutputter().with {
                format = xmlFormat
                output(document, to.newPrintWriter())
            }
            this
        }

        def XmlWriter addAttributes(def Map<String, Tuple2<String, String>> mapToAdd) {
            bulkEdit mapToAdd, { nodes, Tuple2<String, String> value ->
                nodes*.setAttribute(value.first, value.second)
            }
            this
        }

        def XmlWriter addNodesAfter(def Map<String, Tuple2<String, String>> mapToAdd) {
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

        def XmlWriter modifyByXPath(def Map<String, String> mapToModify) {
            bulkEdit mapToModify, { nodes, value ->
                nodes*.value = value
            }
            this
        }

        def private bulkEdit(Map<String, ?> map, Closure callback) {
            map?.each {
                callback xPathFactory.compile(it.key).evaluate(document), it.value
            }
        }

    }
}
