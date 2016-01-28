package com.sdl.webapp.tridion.contextengine;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class AbstractAdfContextClaimsProvider implements ContextClaimsProvider {

    private static final String TAF_CLAIM_CONTEXT = "taf:claim:context:";
    private static final Pattern GT_PATTERN = Pattern.compile(">", Pattern.LITERAL);
    private static final Pattern LT_PATTERN = Pattern.compile("<", Pattern.LITERAL);

    private static boolean isInFamily(String expectedValue, String claimValueForUri) {
        boolean inFamily;
        if (expectedValue.startsWith("<")) {
            int value = Integer.parseInt(LT_PATTERN.matcher(expectedValue).replaceAll(Matcher.quoteReplacement("")));
            int claimValue = Integer.parseInt(claimValueForUri);

            inFamily = !(claimValue >= value);
        } else if (expectedValue.startsWith(">")) {
            int value = Integer.parseInt(GT_PATTERN.matcher(expectedValue).replaceAll(Matcher.quoteReplacement("")));
            int claimValue = Integer.parseInt(claimValueForUri);

            inFamily = !(claimValue <= value);
        } else {
            inFamily = Objects.equal(claimValueForUri, expectedValue);
        }
        return inFamily;
    }

    private static String appendAspectName(String aspectName) {
        String claimNamePrefix = TAF_CLAIM_CONTEXT;
        if (!Strings.isNullOrEmpty(aspectName)) {
            claimNamePrefix += aspectName + ':';
        }
        return claimNamePrefix;
    }

    @Override
    public Map<String, Object> getContextClaims(String aspectName) {
        String claimNamePrefix = appendAspectName(aspectName);

        Set<Entry<URI, Object>> entries = getCurrentClaims().entrySet();
        Map<String, Object> result = new HashMap<>(entries.size());
        for (Entry<URI, Object> claim : entries) {
            String claimName = claim.getKey().toString();
            if (!claimName.startsWith(claimNamePrefix)) {
                continue;
            }

            String propertyName = claimName.substring(TAF_CLAIM_CONTEXT.length()).replace(':', '.');
            result.put(propertyName, claim.getValue());
        }

        return result;
    }

    @Override
    public String getDeviceFamily() {
        // TODO TSI-789: this functionality overlaps with "Context Expressions".

        Resource familiesFile = ApplicationContextHolder.getContext().getResource("classpath:families.xml");
        if (!familiesFile.exists()) {
            log.info("Families.xml file not found, returning null");
            return null;
        }

        Document families;
        try {
            families = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(familiesFile.getFile());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("Error parsing families.xml, returning null", e);
            return null;
        }

        String result = null;

        NodeList deviceFamilies = families.getElementsByTagName("devicefamily");
        for (int i = 0; i < deviceFamilies.getLength(); i++) {
            Element el = (Element) deviceFamilies.item(i);

            NodeList familyConditions = el.getElementsByTagName("condition");
            for (int j = 0; j < familyConditions.getLength(); j++) {
                Element c = (Element) familyConditions.item(j);

                URI uri;
                try {
                    //noinspection ObjectAllocationInLoop
                    uri = new URI(c.getAttribute("uri"));
                } catch (URISyntaxException e) {
                    log.error("Invalid TAF URI : {}", c.getAttribute("uri"), e);
                    continue;
                }

                String expectedValue = c.getAttribute("value");
                String claimValueForUri = getClaimValueForURI(uri);

                // if not then move on to next family
                if (!Strings.isNullOrEmpty(claimValueForUri) &&
                        isInFamily(expectedValue, claimValueForUri)) {
                    result = el.getAttribute("name");
                    break;
                }
            }
        }

        return result;
    }

    protected abstract Map<URI, Object> getCurrentClaims();

    protected abstract String getClaimValueForURI(URI uri);
}
