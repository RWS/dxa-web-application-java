package org.dd4t.core.util;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rogier Oudshoorn, Raimond Kempees
 */
public class XSLTransformer {
    private static final XSLTransformer INSTANCE = new XSLTransformer();
    private static final ConcurrentHashMap<String, Templates> CACHE = new ConcurrentHashMap<>();
    private final boolean cacheTemplates = true;

    private XSLTransformer() {
        // point the transformerfactory towards Xalan XSLTC
        // smartFactory. This factory will generate XSLTC
        // COMPILED templates, and INTERPRETED transformers
        String key = "javax.xml.transform.TransformerFactory";
        String value = "org.apache.xalan.xsltc.trax.SmartTransformerFactoryImpl";
        Properties props = System.getProperties();
        props.put(key, value);
        System.setProperties(props);
    }

    public static XSLTransformer getInstance() {
        return INSTANCE;
    }

    public String transformSourceFromFilesource(String source, String resource, Map<String, Object> params) throws TransformerException {
        // attain writer to place result in
        CharArrayWriter caw = new CharArrayWriter();
        StreamResult result = new StreamResult(caw);

        // get XSL transformer
        Transformer trans = getTransformer(resource);
        for (String key : params.keySet()) {
            trans.setParameter(key, params.get(key));
        }

        // if found, transform
        if (trans != null) {
            StringReader reader = new StringReader(source);
            StreamSource xmlSource = new StreamSource(reader);
            // transform
            trans.transform(xmlSource, result);
        }

        return caw.toString();
    }

    public String transformSourceFromFilesource(String source, String resource) throws TransformerException {
        return transformSourceFromFilesource(source, resource, new HashMap<String, Object>());
    }


    /*
     * Function retrieves a Transformer based on given inputstream.
     * If possible, it uses a CACHE.
     */
    private Transformer getTransformer(String resource) throws TransformerConfigurationException {
        Transformer trans = null;
        Templates temp = null;

        // first, lets get the template
        if (cacheTemplates && CACHE.containsKey(resource)) {
            temp = CACHE.get(resource);
        } else {

            InputStream stream = getClass().getResourceAsStream(resource);
            if (null == stream) {
                throw new TransformerConfigurationException("Resource '" + resource + "' could not be loaded. ");
            }
            StreamSource source = new StreamSource(stream);

            // instantiate a transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            temp = transformerFactory.newTemplates(source);

            CACHE.put(resource, temp);
        }

        trans = temp.newTransformer();
        return trans;
    }
}
