package com.sdl.webapp.tridion.contextengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.util.XMLUtils;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;

@Component
public class AdfContextClaimsProvider implements ContextClaimsProvider {

    private final String ContextClaimPrefix = "taf:claim:context:";

    private static final Logger LOG = LoggerFactory.getLogger(AdfContextClaimsProvider.class);


    @Override
    public Map<String, Object> getContextClaims(String aspectName) {
        String claimNamePrefix = ContextClaimPrefix;
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
                String propertyName = claimName.substring(ContextClaimPrefix.length()).replace(':', '.');
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
        } catch (ParserConfigurationException e) {
            LOG.error("Error parsing Families.xml", e);
        } catch (SAXException e) {
            LOG.error("Error parsing Families.xml", e);
        } catch (IOException e) {
            LOG.error("Error loading Families.xml", e);
        }
        return result;
    }
}
