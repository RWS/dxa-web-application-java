package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sdl.webapp.common.util.CacheUtils.memorize;

@Slf4j
@Component
@Scope(value = "request")
public class ContextEngineImpl implements ContextEngine {

    private static Map<String, Map<String, Evaluator>> deviceFamiliesRules;

    private Map<Class, ContextClaims> stronglyTypedClaims = new HashMap<>();

    @Value("${dxa.context.deviceFamilies.file}")
    private String deviceFamiliesFile;

    @Autowired
    private ContextClaimsProvider provider;

    @Getter(lazy = true)
    private final Map<String, Object> claims = claims();

    @Getter(lazy = true)
    private final String deviceFamily = deviceFamily();

    @PostConstruct
    private void init() {
        readDeviceFamiliesFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends ContextClaims> T getClaims(Class<T> cls) {
        if (this.stronglyTypedClaims.containsKey(cls)) {
            return (T) this.stronglyTypedClaims.get(cls);
        }

        try {
            ContextClaims result = memorize(stronglyTypedClaims, cls, cls.newInstance());
            result.setClaims(getClaims());
            return (T) result;
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Exception during getClaims()", e);
            return null;
        }
    }

    private Map<String, Object> claims() {
        try {
            return provider.getContextClaims(null);
        } catch (DxaException e) {
            log.error("Exception getting claims from provider {}", provider, e);
            return null;
        }
    }

    private String deviceFamily() {
        String result = provider.getDeviceFamily();
        if (result != null) {
            return result;
        }

        result = defineDeviceFamily();
        if (result != null) {
            return result;
        }

        return fallbackDeviceFamily();
    }

    private String defineDeviceFamily() {
        if (deviceFamiliesRules != null) {
            for (Map.Entry<String, Map<String, Evaluator>> family : deviceFamiliesRules.entrySet()) {
                if (isInFamily(family.getValue())) {
                    log.debug("This is '{}' because of conditions {}", family.getKey(), family.getValue());
                    return family.getKey();
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private boolean isInFamily(Map<String, Evaluator> families) {
        for (Map.Entry<String, Evaluator> conditions : families.entrySet()) {
            if (!getClaims().containsKey(conditions.getKey()) ||
                    !conditions.getValue().evaluate(getClaims().get(conditions.getKey()))) {
                return false;
            }
        }
        return true;
    }

    private String fallbackDeviceFamily() {
        DeviceClaims claims = getClaims(DeviceClaims.class);
        if (!claims.getIsMobile() && !claims.getIsTablet()) {
            return "desktop";
        }
        if (claims.getIsTablet()) {
            return "tablet";
        }
        if (claims.getIsMobile() && !claims.getIsTablet()) {
            return claims.getDisplayWidth() > 319 ? "smartphone" : "featurephone";
        }

        return null;
    }

    private void readDeviceFamiliesFile() {
        if (deviceFamiliesRules != null) {
            return;
        }

        ClassPathResource resource = new ClassPathResource(deviceFamiliesFile);
        if (!resource.exists()) {
            log.info("Device families file is not found, fallback to default");
            return;
        }

        deviceFamiliesRules = new LinkedHashMap<>();

        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(resource.getInputStream());
            document.normalizeDocument();

            NodeList elFamilies = document.getElementsByTagName("devicefamily");
            for (int i = 0; i < elFamilies.getLength(); i++) {
                Node node = elFamilies.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elFamily = (Element) node;

                    HashMap<String, Evaluator> conditions = new HashMap<>();
                    String deviceFamily = elFamily.getAttribute("name");
                    deviceFamiliesRules.put(deviceFamily, conditions);

                    NodeList elConditions = elFamily.getElementsByTagName("condition");
                    for (int j = 0; j < elConditions.getLength(); j++) {
                        Node item = elConditions.item(j);
                        if (item.getNodeType() == Node.ELEMENT_NODE) {
                            Element elCondition = (Element) item;

                            String contextClaim = elCondition.getAttribute("context-claim");
                            String value = elCondition.getAttribute("value");
                            log.debug("Adding a condition {} <> {} for {}", contextClaim, value, deviceFamily);
                            conditions.put(contextClaim, Evaluator.getByExpectedValue(value));
                        }
                    }
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @ToString
    private static abstract class Evaluator<T> {

        T expected;

        Sign sign;

        Evaluator(T expected, Sign sign) {
            this.expected = expected;
            this.sign = sign;
        }

        static Evaluator getByExpectedValue(final String conditionValue) {
            if (tryBoolean(conditionValue)) {
                return new Evaluator<Boolean>(Boolean.parseBoolean(conditionValue), Sign.EQUAL) {
                    @Override
                    boolean evaluate(Boolean value) {
                        return Objects.equals(expected, value);
                    }
                };
            }

            Pattern pattern = Pattern.compile("([<>])(\\d+)");
            final Matcher matcher = pattern.matcher(conditionValue);
            if (matcher.matches() && matcher.groupCount() == 2) {
                return new Evaluator<Integer>(Integer.parseInt(matcher.group(2)), Sign.gtOrLt(matcher.group(1))) {
                    @Override
                    boolean evaluate(Integer value) {
                        return ">".equalsIgnoreCase(matcher.group(1)) ? value > expected : value < expected;
                    }
                };
            }

            return new Evaluator<String>(conditionValue, Sign.EQUAL) {
                @Override
                boolean evaluate(String value) {
                    return Objects.equals(value, conditionValue);
                }
            };
        }

        private static boolean tryBoolean(String conditionValue) {
            return "true".equalsIgnoreCase(conditionValue) || "false".equalsIgnoreCase(conditionValue);
        }

        abstract boolean evaluate(T value);

        /**
         * Sign is needed only for #toString() and logging purposes.
         */
        private enum Sign {
            EQUAL, GT, LT;

            static Sign gtOrLt(String s) {
                return "<".equals(s) ? LT : GT;
            }
        }
    }
}
