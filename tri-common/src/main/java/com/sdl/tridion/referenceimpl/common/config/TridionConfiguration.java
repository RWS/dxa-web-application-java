package com.sdl.tridion.referenceimpl.common.config;

import com.google.common.base.Strings;
import com.sdl.tridion.referenceimpl.common.util.NodeListAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TridionConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TridionConfiguration.class);

    private static final String PUBLICATIONS_XPATH = "/Configuration/URLMappings/StaticMappings/Publications/Publication";

    @Value("classpath:/cd_dynamic_conf.xml")
    private Resource cdDynamicConf;

    private Map<Integer, Localization> localizations;

    public synchronized Map<Integer, Localization> getLocalizations() {
        if (localizations == null) {
            try {
                loadLocalizations();
            } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException e) {
                throw new RuntimeException("Error while loading localizations", e);
            }
        }
        return localizations;
    }

    private void loadLocalizations() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        localizations = new LinkedHashMap<>();

        final Document document;
        try (InputStream in = cdDynamicConf.getInputStream()) {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
        }

        final NodeList nodeList = (NodeList) XPathFactory.newInstance().newXPath()
                .evaluate(PUBLICATIONS_XPATH, document, XPathConstants.NODESET);

        for (Node publicationNode : new NodeListAdapter(nodeList)) {
            Localization.Builder builder = Localization.newBuilder();
            final int publicationId = Integer.parseInt(getAttribute(publicationNode, "Id"));
            builder.setLocalizationId(publicationId);

            Node hostNode = getChildNode(publicationNode.getChildNodes(), "Host");

            final String domain = getAttribute(hostNode, "Domain");
            if (!Strings.isNullOrEmpty(domain)) {
                builder.setDomain(domain.toLowerCase());
            }

            String portString = getAttribute(hostNode, "Port");
            Integer port = Strings.isNullOrEmpty(portString) ? null : Integer.valueOf(portString);

            localizations.put(publicationId, builder
                    .setPort(port)
                    .setPath(getAttribute(hostNode, "Path"))
                    .setProtocol(getAttribute(hostNode, "Protocol"))
                    .build());

            // TODO: What about locale?
        }
    }

    private String getAttribute(Node node, String attributeName) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            final Node attribute = attributes.getNamedItem(attributeName);
            if (attribute != null) {
                return attribute.getTextContent();
            }
        }
        return null;
    }

    private Node getChildNode(NodeList nodeList, String childNodeName) {
        for (Node childNode : new NodeListAdapter(nodeList)) {
            if (childNode.getNodeName().equals(childNodeName)) {
                return childNode;
            }
        }
        return null;
    }
}
