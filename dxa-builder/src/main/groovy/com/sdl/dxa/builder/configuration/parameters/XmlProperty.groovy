package com.sdl.dxa.builder.configuration.parameters

import com.sdl.dxa.builder.configuration.writers.XmlWriter
import org.jdom2.Namespace

class XmlProperty extends Property {

    private Map<String, String> xmlAddNodesMap = [:]
    private Map<String, String> xmlAddAttributesMap = [:]
    private String[] xmlModifyArray = []

    Namespace namespace
    boolean canBeEmpty
    Map<String, Tuple2<String, String>> addNodesMap = [:]
    Map<String, Tuple2<String, String>> addAttributesMap = [:]
    Map<String, String> modifyMap = [:]

    XmlProperty thatAddsXmlNodes(Map<String, String> xmlAddNodesMap) {
        this.xmlAddNodesMap = xmlAddNodesMap
        this
    }

    XmlProperty thatAddsXmlAttributes(Map<String, String> xmlAddAttributesMap) {
        this.xmlAddAttributesMap = xmlAddAttributesMap
        this
    }

    XmlProperty thatModifiesXml(String... xmlModifyNodes) {
        this.xmlModifyArray = xmlModifyNodes
        this
    }

    XmlProperty thatModifiesXml(Namespace namespace, String... xmlModifyNodes) {
        this.xmlModifyArray = xmlModifyNodes
        this.namespace = namespace
        this
    }

    XmlProperty thatCanBeEmpty() {
        this.canBeEmpty = true
        this
    }

    /**
     * @see com.sdl.dxa.builder.configuration.writers.XmlWriter
     */
    XmlProperty acceptValue(String value) {
        addNodesMap = toTuples(xmlAddNodesMap, value)
        addAttributesMap = toTuples(xmlAddAttributesMap, value)
        modifyMap = xmlModifyArray.collectEntries { [(it): value] }

        this
    }

    @Override
    def processProperty(String filename, String value) {
        if (value == null) {
            return
        }
        if (this.isCanBeEmpty() || value) {
            println "Modified $filename, set ${name == null ? 'property to ' : (name + ' = ')} $value"
            new XmlWriter().from(filename).to(filename).format('raw')
                    .open()
                    .modify(this.acceptValue(value))
                    .save()
        }
    }

    private static Map<String, Tuple2<String, String>> toTuples(Map<String, String> initial, String value) {
        initial.collectEntries { String key, String val ->
            [(key): new Tuple2<>(val, value)]
        } as Map<String, Tuple2<String, String>>
    }
}
