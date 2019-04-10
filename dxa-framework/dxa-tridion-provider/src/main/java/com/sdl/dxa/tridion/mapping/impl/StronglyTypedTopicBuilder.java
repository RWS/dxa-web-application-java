package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.tridion.mapping.EntityModelBuilder;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.GenericTopic;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.exceptions.DxaException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Model Builder used to convert {@link GenericTopic} Entity Models to Strongly Typed Topic Models.
 *
 * This class has two use cases:
 *
 * * It can act as an Entity Model Builder which is configured in the {@link com.sdl.dxa.tridion.mapping.ModelBuilderPipeline}.
 * * It can be used directly to convert a given {@link GenericTopic}. See {@link #tryConvertToStronglyTypedTopic(GenericTopic, Class)}.
 *
 */
@Service
@Profile("!dxa.generictopic.disabled")
public class StronglyTypedTopicBuilder implements EntityModelBuilder {
    private static String SELF = "_self";

    private static final Logger LOG = LoggerFactory.getLogger(StronglyTypedTopicBuilder.class);

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @Autowired
    private SemanticMappingRegistry semanticMappingRegistry;

    @Autowired
    private WebRequestContext webRequestContext;

    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private ThreadLocal<DocumentBuilder> documentBuilderThreadLocal = new ThreadLocal<>();
    private ThreadLocal<Transformer> transformerThreadLocal = new ThreadLocal<>();
    private XPathFactory xPathFactory = XPathFactory.newInstance();
    private final XPathExpression linkXPath;
    private ThreadLocal<Map<String, XPathExpression>> xpathExpressionCacheThreadLocal = new ThreadLocal<>();

    public StronglyTypedTopicBuilder() throws XPathExpressionException {
        linkXPath = getXpathExpression(".//a");
    }

    /**
     * Tries to convert a given generic Topic to a Strongly Typed Topic Model.
     *
     * @param genericTopic The generic Topic to convert.
     * @param ofType The type of the Strongly Typed Topic Model to convert to. If not specified (or null), the type will be determined from the XHTML.
     * @returns The Strongly Typed Topic Model or null if the generic Topic cannot be converted.
     */
    public <T extends EntityModel> T tryConvertToStronglyTypedTopic(GenericTopic genericTopic, Class<T> ofType) throws DxaException {
        LOG.debug("Trying to convert {} to Strongly Typed Topic Model...", genericTopic);

        Map<String, Field> registeredTopicTypes = semanticMappingRegistry.getEntitiesByVocabulary(SemanticVocabulary.SDL_DITA);
        if (registeredTopicTypes.isEmpty()) {
            LOG.debug("No Strongly Typed Topic Models registered.");
            return null;
        }

        Element rootElement = null;
        try {
            rootElement = parseXhtml(genericTopic);
        } catch (Exception ex) {
            LOG.error("Unable to parse generic Topic XHTML. Topic body: \"" + genericTopic.getTopicBody() +"\"", ex);
            return null;
        }

        Class<T> topicType = ofType;
        if (ofType == null) {
            topicType = determineTopicType(rootElement, registeredTopicTypes);
            if (topicType == null) {
                LOG.debug("No matching Strongly Typed Topic Model found.");
                return null;
            }
        }

        T stronglyTypedTopic = null;
        try {
            stronglyTypedTopic = buildStronglyTypedTopic(topicType, rootElement);
        } catch (IllegalAccessException e) {
            throw new DxaException("Could not build strongly typed topic", e);
        } catch (InstantiationException e) {
            throw new DxaException("Could not instantiate strongly typed topic", e);
        }

        if (stronglyTypedTopic.getId() == null)
            ((AbstractEntityModel) stronglyTypedTopic).setId(genericTopic.getId());

        return stronglyTypedTopic;
    }

    protected Element parseXhtml(GenericTopic genericTopic) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = getBuilder();
        StringBuilder xmlStringBuilder = new StringBuilder();
        xmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        xmlStringBuilder.append("<topic>");
        xmlStringBuilder.append(genericTopic.getTopicBody());
        xmlStringBuilder.append("</topic>");
        Document topicXmlDoc = builder.parse(new InputSource(new StringReader(xmlStringBuilder.toString())));

        Element topicElement = topicXmlDoc.getDocumentElement();

        // Inject GenericTopic's TopicTitle as additional HTML element
        Element topicTitleElement = topicXmlDoc.createElement("h1");
        topicTitleElement.setAttribute("class", "_topicTitle");
        topicTitleElement.setTextContent(genericTopic.getTopicTitle());
        topicElement.appendChild(topicTitleElement);

        return topicElement;
    }

    /**
     * Get the Transformer that belongs to this thread.
     * This way we can reuse instances of Transformer without worrying about thread-safety
     *
     * @return A Transformer for this thread
     * @throws ParserConfigurationException
     */
    private Transformer getTransformer() throws TransformerConfigurationException {
        Transformer transformer = transformerThreadLocal.get();
        if (transformer == null) {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            transformer = transFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformerThreadLocal.set(transformer);
        }
        return transformer;
    }

    /**
     * Get the DocumentBuilder that belongs to this thread.
     * This way we can reuse instances of DocumentBuilders without worrying about thread-safety
     *
     * @return A DocumentBuilder for this thread
     * @throws ParserConfigurationException
     */
    private DocumentBuilder getBuilder() throws ParserConfigurationException {
        DocumentBuilder documentBuilder = documentBuilderThreadLocal.get();
        if (documentBuilder == null) {
            documentBuilder = factory.newDocumentBuilder();
            documentBuilderThreadLocal.set(documentBuilder);
        }
        return documentBuilder;
    }

    /**
     * Get a XPathExpression for a string.
     * This implementation ensures that you get a XPathExpression that is specific for this thread.
     *
     * @param xPath
     * @return a XpathExpression
     * @throws XPathExpressionException
     */
    private XPathExpression getXpathExpression(String xPath) throws XPathExpressionException {
        Map<String, XPathExpression> xpathExpressionCache = xpathExpressionCacheThreadLocal.get();
        if (xpathExpressionCache == null) {
            xpathExpressionCache = new HashMap<>();
            xpathExpressionCacheThreadLocal.set(xpathExpressionCache);
        }

        XPathExpression result = xpathExpressionCache.get(xPath);
        if (result == null) {
            result = xPathFactory.newXPath().compile(xPath);
            xpathExpressionCache.put(xPath, result);
        }
        return result;
    }

    protected String getPropertyXPath(String propertyName) {
        if (SELF.equals(propertyName))
            return ".";

        String[] propertyNameSegments = propertyName.split("/");
        StringBuilder xPathBuilder = new StringBuilder(".");
        for (String propertyNameSegment : propertyNameSegments) {
            xPathBuilder.append("//*[contains(@class, '" + propertyNameSegment + "')]");
        }
        return xPathBuilder.toString();
    }

    /**
    * Filters the XHTML elements found by the XPath query.
     *
    * Because we use "contains" in the XPath, it may match on part of a class name.
    * We filter out any partial matches here.
     */
    protected List<Element> filterXPathResults(NodeList htmlNodes, String ditaPropertyName) {
        if (htmlNodes == null || htmlNodes.getLength() == 0)
            return null;

        List<Element> result = new ArrayList<>(htmlNodes.getLength());

        if (ditaPropertyName.equals(SELF)) {
            for (int i = 0; i < htmlNodes.getLength(); i++) {
                result.add((Element) htmlNodes.item(i));
            }
            return result;
        }

        // Only look at last path segment
        int lastSlashPos = ditaPropertyName.lastIndexOf('/');
        if (lastSlashPos >= 0)
            ditaPropertyName = ditaPropertyName.substring(lastSlashPos + 1);

        for (int i = 0; i < htmlNodes.getLength(); i++) {
            Node htmlNode = htmlNodes.item(i);
            if (htmlNode.getNodeType() == Node.ELEMENT_NODE) {
                Element htmlElement = (Element) htmlNode;

                String[] classes = htmlElement.getAttribute("class").split(" ");
                if (new HashSet(Arrays.asList(classes)).contains(ditaPropertyName)) {
                    result.add(htmlElement);
                }
            }
        }
        return result;
    }

    protected <T extends ViewModel> Class<T> determineTopicType(Element rootElement, Map<String, Field> registeredTopicTypes) {
        Class<T> bestMatch = null;
        int bestMatchClassPos = -1;

        for (Map.Entry<String, Field> tuple : registeredTopicTypes.entrySet()) {
            String propertyName = tuple.getKey();
            Field modelType = tuple.getValue();

            if (propertyName == null || propertyName.isEmpty()) {
                LOG.debug("Skipping Type '" + modelType.getDeclaringClass().getName() + "' (no EntityName specified).");
                continue;
            }

            String xPath = getPropertyXPath(propertyName);
            XPathExpression xpathToTry = null;
            Element matchedElement = null;

            try {
                xpathToTry = getXpathExpression(xPath);

                LOG.debug("Trying XPath \"" + xPath + "\" for type '" + modelType.getDeclaringClass().getName() + "'");
                matchedElement = (Element) xpathToTry.evaluate(rootElement, XPathConstants.NODE);
            } catch (XPathExpressionException e) {
                LOG.warn("Error evaluating Xpath", e);
            }

            if (matchedElement != null) {
                LOG.debug("Matching XHTML element found.");
                int classPos = matchedElement.getAttribute("class").indexOf(propertyName);
                if (classPos > bestMatchClassPos) {
                    bestMatch = (Class<T>) modelType.getDeclaringClass();
                    bestMatchClassPos = classPos;
                }
            } else {
                LOG.debug("No matching XHTML element found.");
            }
        }

        return bestMatch;
    }


    protected <E extends EntityModel> E buildStronglyTypedTopic(Class<E> modelType, Element htmlElement) throws IllegalAccessException, InstantiationException {
        LOG.debug("Building Strongly Typed Topic Model '" + modelType.getSimpleName() + "'...");
        E result = modelType.newInstance();

        mapBaseProperties((AbstractEntityModel) result, htmlElement);
        mapSemanticProperties((AbstractEntityModel) result, htmlElement);

        // Let the View Model determine the View to be used.
        // Do this after mapping all properties so that the View name can be derived from the properties if needed.
        result.setMvcData(((AbstractEntityModel) result).getDefaultMvcData());

        return result;
    }

    protected void mapBaseProperties(AbstractEntityModel stronglyTypedTopic, Element htmlElement) {
        Attr id = htmlElement.getAttributeNode("id");
        if (id != null) {
            stronglyTypedTopic.setId(id.getValue());
        }
        Attr aClass = htmlElement.getAttributeNode("class");
        if (aClass != null) {
            stronglyTypedTopic.setHtmlClasses(aClass.getValue());
        }
    }

    protected void mapSemanticProperties(AbstractEntityModel stronglyTypedTopic, Element rootElement) {
        // Map all the fields (including fields inherited from superclasses) of the entity
        ReflectionUtils.doWithFields(stronglyTypedTopic.getClass(), field -> {
            // Find the semantics for this field
            final Set<FieldSemantics> registrySemantics = semanticMappingRegistry.getFieldSemantics(field);

            List<Element> htmlElements = null;
            for (FieldSemantics fieldSemantics : registrySemantics) {
                String ditaPropertyName = fieldSemantics.getPropertyName();
                String propertyXPath = getPropertyXPath(ditaPropertyName);
                XPathExpression xpathToTry = null;
                NodeList xPathResults = null;

                try {
                    xpathToTry = getXpathExpression(propertyXPath);

                    LOG.debug("Trying XPath \"" + propertyXPath + "\" for property '" + fieldSemantics.getPropertyName() + "'");
                    xPathResults = (NodeList) xpathToTry.evaluate(rootElement, XPathConstants.NODESET);
                    htmlElements = filterXPathResults(xPathResults, ditaPropertyName);
                    if (htmlElements != null && !htmlElements.isEmpty())
                        break;
                    LOG.debug("No XHTML elements found for DITA property '" + ditaPropertyName + "'.");
                } catch (XPathExpressionException e) {
                    LOG.warn("Failed to evaluate Xpath", e);
                }
                if (htmlElements == null || htmlElements.isEmpty()) {
                    LOG.debug("Unable to map property '" + fieldSemantics.getPropertyName() + "'");
                    continue;
                }
                LOG.debug(htmlElements.size() + " XHTML elements found.");
            }
            try {
                Object propertyValue = getPropertyValue(field, htmlElements);
                field.set(stronglyTypedTopic, propertyValue);
            } catch (Exception ex) {
                LOG.warn("Unable to map property " + field.getDeclaringClass().getSimpleName() + "." + field.getName(), ex);
            }
        });
    }

    protected Object getPropertyValue(Field modelPropertyType, List<Element> htmlElements) throws InstantiationException, IllegalAccessException, DxaException {
        Class targetType = modelPropertyType.getType();
        boolean isListProperty = targetType.isAssignableFrom(List.class);
        Object result;

        if (isListProperty) {
            ParameterizedType genericType = (ParameterizedType) modelPropertyType.getGenericType();
            targetType = (Class) genericType.getActualTypeArguments()[0];
            List list = new ArrayList<>();
            for (Element element: htmlElements) {
                list.add(convertElement(element, targetType));
            }
            result = list;
        } else {
            result = convertElement(htmlElements.get(0), targetType);
        }

        return result;
    }

    private Object convertElement(Element element, Class targetType) throws DxaException, InstantiationException, IllegalAccessException {
        if (targetType.equals(String.class)) {
            return element.getTextContent();
        } else if (targetType.equals(RichText.class)) {
            return buildRichText(element);
        } else if (targetType.equals(Link.class)) {
            return buildLink(element);
        } else if (EntityModel.class.isAssignableFrom(targetType)) {
            return buildStronglyTypedTopic(targetType, element);
        } else {
            throw new DxaException("Unexpected property type '" + targetType.getSimpleName() + "'");
        }
    }

    protected RichText buildRichText(Element element) {
        try {
            Transformer transformer = getTransformer();
            StringWriter buffer = new StringWriter();
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                transformer.transform(new DOMSource(childNodes.item(i)),
                        new StreamResult(buffer));
            }
            String str = buffer.toString();
            return new RichText(str);
        } catch (TransformerException e) {
            LOG.warn("Could not convert to RichText", e);
        }

        return null;
    }

    protected Link buildLink(Element htmlElement) {
        Element hyperlink = null;
        if ("a".equals(htmlElement.getTagName())) {
            hyperlink = htmlElement;
        } else {
            try {
                hyperlink = (Element) linkXPath.evaluate(htmlElement, XPathConstants.NODE);
            } catch (XPathExpressionException e) {
                LOG.warn("Couldn't find <a> tag", e);
            }
            if (hyperlink == null) {
                LOG.debug("No hyperlink found in XHTML element: {}", htmlElement);
                return null;
            }
        }

        String href = hyperlink.getAttribute("href");
        String textContent = hyperlink.getTextContent();

        String alternateText = null;
        Attr title = hyperlink.getAttributeNode("title");
        if (title != null) {
            alternateText = title.getValue();
        }

        Link link = new Link();
        link.setUrl(href);
        link.setLinkText(textContent);
        link.setAlternateText(alternateText);
        return link;
    }

    /**
     * Builds a strongly typed Entity Model based on a given DXA R2 Data Model.
     *
     * @param entityModel The strongly typed Entity Model to build. Is null for the first Entity Model Builder in the pipeline.
     * @param entityModelData The DXA R2 Data Model.
     */
    @Override
    public <T extends EntityModel> T buildEntityModel(@Nullable T entityModel, EntityModelData entityModelData, @Nullable Class<T> expectedClass) throws DxaException {
        if (entityModel == null) {
            throw new DxaException("The " + this.getClass().getSimpleName() + " must be configured after the DefaultModelBuilder.");
        }
        if (entityModel instanceof GenericTopic) {
            GenericTopic genericTopic = (GenericTopic) entityModel;
            LOG.debug("Generic Topic encountered...");
            T stronglyTypedTopic = tryConvertToStronglyTypedTopic(genericTopic, expectedClass);
            if (stronglyTypedTopic != null) {
                LOG.debug("Converted {} to {}" + genericTopic, stronglyTypedTopic);
                return stronglyTypedTopic;
            }
            LOG.debug("Unable to convert {} to Strongly Typed Topic.", genericTopic);
        }

        return entityModel;
    }

    @Override
    public int getOrder() {
        //Just after DefaultModelBuilder (which has Ordered.HIGHEST_PRECEDENCE)
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
