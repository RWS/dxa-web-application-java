/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.core.util;

import javax.xml.transform.*;
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
    private static final Map<String, Templates> CACHE = new ConcurrentHashMap<>();

    private XSLTransformer () {
        // point the transformerfactory towards Xalan XSLTC
        // smartFactory. This factory will generate XSLTC
        // COMPILED templates, and INTERPRETED transformers
        String key = "javax.xml.transform.TransformerFactory";
        String value = "org.apache.xalan.xsltc.trax.SmartTransformerFactoryImpl";
        Properties props = System.getProperties();
        props.put(key, value);
        System.setProperties(props);
    }

    public static XSLTransformer getInstance () {
        return INSTANCE;
    }

    public String transformSourceFromFilesource (String source, String resource, Map<String, Object> params) throws TransformerException {
        // attain writer to place result in
        CharArrayWriter caw = new CharArrayWriter();
        StreamResult result = new StreamResult(caw);

        // get XSL transformer
        Transformer trans = getTransformer(resource);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            trans.setParameter(entry.getKey(), entry.getValue());
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

    public String transformSourceFromFilesource (String source, String resource) throws TransformerException {
        return transformSourceFromFilesource(source, resource, new HashMap<String, Object>());
    }


    /*
     * Function retrieves a Transformer based on given inputstream.
     * If possible, it uses a CACHE.
     */
    private Transformer getTransformer (String resource) throws TransformerConfigurationException {
        Transformer trans = null;
        Templates temp = null;

        // first, lets get the template
        if (CACHE.containsKey(resource)) {
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
