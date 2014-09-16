/**  
 *  Copyright 2011 Capgemini & SDL
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dd4t.core.util;

import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Rogier Oudshoorn, Capgemini NL 
 */
public class XSLTransformer {
    private HashMap<String , Templates> cache = null;
    private boolean cacheTemplates = true;
    private static Logger logger = LoggerFactory.getLogger(XSLTransformer.class);
    
    public XSLTransformer(){
            this.cache = new HashMap<String, Templates>();

            // point the transformerfactory towards Xalan XSLTC
            // smartFactory. This factory will generate XSLTC
            // COMPILED templates, and INTERPRETED transformers
            String key = "javax.xml.transform.TransformerFactory";
            String value = "org.apache.xalan.xsltc.trax.SmartTransformerFactoryImpl";
            Properties props = System.getProperties();
            props.put(key, value);
            System.setProperties(props);
    }
    


    public String transformSourceFromFilesource(String source, String resource, Map<String, Object> params){
        long start = System.currentTimeMillis();
        
        // attain writer to place result in
        CharArrayWriter caw = new CharArrayWriter();
        StreamResult result = new StreamResult(caw);
            
        // get XSL transformer
            Transformer trans = getTransformer(resource);
            for(String key : params.keySet()){
                trans.setParameter(key, params.get(key));                
            }
            
            // if found, transform
            if (trans != null) {
                    try {           
                        StringReader reader = new StringReader(source);
                        StreamSource xmlSource = new StreamSource(reader);

                        // transform
                        trans.transform(xmlSource, result);                                                
                    } catch (Exception ex) {
                        logger.error("Exception performing XSL transformation: "+ex.getMessage(), ex);                                          
                    }
                    catch (Error er){
                        logger.error("Error performing XSL transformation: "+er.getMessage(), er);      
                    }
            }
            
            if(logger.isDebugEnabled()){
                long end = System.currentTimeMillis();
                logger.debug("Transformed resource "+resource+" in "+(end-start)+" milliseconds.");
            }
                            
            return caw.toString();
    }
    
    public String transformSourceFromFilesource(String source, String resource){
        return transformSourceFromFilesource(source, resource, new HashMap<String, Object>());
    }

    
    /*
     * Function retrieves a Transformer based on given inputstream.
     * If possible, it uses a cache.
     */
    private Transformer getTransformer(String resource) {
            Transformer trans = null;
            Templates temp = null;

            // first, lets get the template
            if (cacheTemplates && cache.containsKey(resource)) {
                    temp = cache.get(resource);
            } else {
                    // not cached, search
                    try {                       
                            InputStream stream = getClass().getResourceAsStream(resource);
                            StreamSource source = new StreamSource(stream);
                                                            
                            // instantiate a transformer
                            TransformerFactory transformerFactory = TransformerFactory.newInstance();                            
                            temp = transformerFactory.newTemplates(source);                            

                            // cache template
                            cache.put(resource, temp);

                    } catch (Exception ex) {
                            logger.error("Unable to load XSL : "+ex.getMessage(), ex);
                    }
            }

            try{
                    trans = temp.newTransformer();
            }
            catch(Exception ex){
                    logger.error("Error transformer from template with name "+ resource+".", ex);                    
            }

            return trans;
    }
}
