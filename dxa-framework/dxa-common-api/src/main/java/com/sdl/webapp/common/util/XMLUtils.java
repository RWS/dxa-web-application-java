package com.sdl.webapp.common.util;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * Utilities for working with XML.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class XMLUtils {

    // DocumentBuilder is not guaranteed to be thread-safe
    private static final ThreadLocal<DocumentBuilder> DOCUMENT_BUILDER = new ThreadLocal<DocumentBuilder>() {
        @Override
        protected DocumentBuilder initialValue() {
            try {
                final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                builderFactory.setNamespaceAware(true);
                return builderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new RuntimeException("Error while creating document builder", e);
            }
        }
    };

    // Transformer is not guaranteed to be thread-safe
    private static final ThreadLocal<Transformer> TRANSFORMER = new ThreadLocal<Transformer>() {
        @Override
        protected Transformer initialValue() {
            try {
                return TransformerFactory.newInstance().newTransformer();
            } catch (TransformerConfigurationException e) {
                throw new RuntimeException("Error while creating transformer", e);
            }
        }
    };

    private XMLUtils() {
    }

    /**
     * <p>parse.</p>
     *
     * @param text a {@link java.lang.String} object.
     * @return a {@link org.w3c.dom.Document} object.
     * @throws java.io.IOException      if any.
     * @throws org.xml.sax.SAXException if any.
     */
    public static Document parse(String text) throws IOException, SAXException {
        final DocumentBuilder documentBuilder = DOCUMENT_BUILDER.get();
        final Document document = documentBuilder.parse(new InputSource(new StringReader(text)));
        documentBuilder.reset();
        return document;
    }

    /**
     * <p>format.</p>
     *
     * @param document a {@link org.w3c.dom.Document} object.
     * @return a {@link java.lang.String} object.
     * @throws javax.xml.transform.TransformerException if any.
     */
    public static String format(Document document) throws TransformerException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Transformer transformer = TRANSFORMER.get();
        transformer.transform(new DOMSource(document), new StreamResult(out));
        transformer.reset();
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
