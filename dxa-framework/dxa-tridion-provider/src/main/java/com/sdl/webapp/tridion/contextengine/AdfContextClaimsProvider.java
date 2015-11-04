package com.sdl.webapp.tridion.contextengine;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Component
public class AdfContextClaimsProvider implements ContextClaimsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AdfContextClaimsProvider.class);

    @Override
    public Map<String, Object> getContextClaims(String aspectName) {
        String contextClaimPrefix = "taf:claim:context:";
        String claimNamePrefix = contextClaimPrefix;
        if (!Strings.isNullOrEmpty(aspectName)) {
            claimNamePrefix += aspectName + ":";
        }

        Map<String, Object> result = new HashMap<String, Object>();
        ClaimStore currentClaimStore = AmbientDataContext.getCurrentClaimStore();
        if (currentClaimStore != null) {
            for (Entry<URI, Object> claim : currentClaimStore.getAll().entrySet()) {
                String claimName = claim.getKey().toString();
                if (!claimName.startsWith(claimNamePrefix)) {
                    continue;
                }
                String propertyName = claimName.substring(contextClaimPrefix.length()).replace(':', '.');
                result.put(propertyName, claim.getValue());
            }
        }
        return result;

    }

    public String getDeviceFamily() {
        // TODO TSI-789: this functionality overlaps with "Context Expressions".
        ClassLoader classLoader = getClass().getClassLoader();
        URL familiesFile = classLoader.getResource("families.xml");
        if (familiesFile == null) {
            LOG.debug("Families.xml file not found, returning null");
            return null;
        }
        File file = new File(familiesFile.getFile());
        if (!file.exists()) {
            LOG.debug("Families.xml file not found, returning null");
            return null;
        }
        String result = null;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document families = documentBuilder.parse(file);
            NodeList deviceFamilies = families.getElementsByTagName("devicefamily");
            for (int i = 0; i < deviceFamilies.getLength(); i++) {
                Element n = (Element) deviceFamilies.item(i);
                String family = n.getAttribute("name");
                boolean inFamily = true;
                NodeList familyConditions = n.getElementsByTagName("condition");
                for (int j = 0; j < familyConditions.getLength(); j++) {
                    Element c = (Element) familyConditions.item(j);
                    try {
                        URI uri = new URI(c.getAttribute("uri"));
                        String expectedValue = c.getAttribute("value");
                        if (expectedValue.startsWith("<")) {
                            int value = Integer.parseInt(expectedValue.replace("<", ""));
                            int claimValue = Integer.parseInt(AmbientDataContext.getCurrentClaimStore().get(uri, String.class));
                            if (claimValue >= value)
                                inFamily = false;
                        } else if (expectedValue.startsWith(">")) {
                            int value = Integer.parseInt(expectedValue.replace(">", ""));
                            int claimValue = Integer.parseInt(AmbientDataContext.getCurrentClaimStore().get(uri, String.class));
                            if (claimValue <= value)
                                inFamily = false;
                        } else {
                            String stringClaimValue = AmbientDataContext.getCurrentClaimStore().get(uri, String.class);
                            if (!stringClaimValue.equals(expectedValue))
                                inFamily = false; // move on to next family
                        }
                    } catch (URISyntaxException e) {
                        LOG.error("Invalid TAF URI : " + c.getAttribute("uri"), e);
                        inFamily = false;
                    }
                    if (inFamily) {
                        result = family;
                        break;
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e){
            LOG.error("Error parsing Families.xml", e);
        }
        return result;
    }
}
