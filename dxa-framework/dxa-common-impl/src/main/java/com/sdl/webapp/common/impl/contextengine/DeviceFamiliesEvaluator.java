package com.sdl.webapp.common.impl.contextengine;

import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Component
public class DeviceFamiliesEvaluator {

    @Value("${dxa.context.deviceFamilies.file}")
    private String deviceFamiliesFile;

    private Map<String, Map<String, Evaluator>> deviceFamiliesRules;

    @PostConstruct
    public void init() {
        readDeviceFamiliesFile();
    }

    @Nullable String defineDeviceFamily(Map<String, Object> claims) {
        if (deviceFamiliesRules != null) {
            for (Map.Entry<String, Map<String, DeviceFamiliesEvaluator.Evaluator>> family : deviceFamiliesRules.entrySet()) {
                if (isInFamily(claims, family.getValue())) {
                    log.debug("This is '{}' because of conditions {}", family.getKey(), family.getValue());
                    return family.getKey();
                }
            }
        }
        return null;
    }

    String fallbackDeviceFamily(DeviceClaims deviceClaims) {
        Boolean isTablet = deviceClaims.getIsTablet();
        if (isTablet != null && isTablet) {
            return "tablet";
        }

        Boolean isMobile = deviceClaims.getIsMobile();
        if (isMobile != null && isMobile) {
            Integer displayWidth = deviceClaims.getDisplayWidth();
            if (displayWidth != null) {
                return displayWidth > 319 ? "smartphone" : "featurephone";
            }
        }

        return "desktop";
    }

    @SuppressWarnings("unchecked")
    private boolean isInFamily(@NonNull Map<String, Object> claims,
                               @NonNull Map<String, DeviceFamiliesEvaluator.Evaluator> families) {
        for (Map.Entry<String, DeviceFamiliesEvaluator.Evaluator> conditions : families.entrySet()) {
            if (!claims.containsKey(conditions.getKey()) ||
                    !conditions.getValue().evaluate(claims.get(conditions.getKey()))) {
                return false;
            }
        }
        return true;
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
                    String elDeviceFamily = elFamily.getAttribute("name");
                    deviceFamiliesRules.put(elDeviceFamily, conditions);

                    NodeList elConditions = elFamily.getElementsByTagName("condition");
                    for (int j = 0; j < elConditions.getLength(); j++) {
                        Node item = elConditions.item(j);
                        if (item.getNodeType() == Node.ELEMENT_NODE) {
                            Element elCondition = (Element) item;

                            String contextClaim = elCondition.getAttribute("context-claim");
                            String value = elCondition.getAttribute("value");
                            log.debug("Adding a condition {} <> {} for {}", contextClaim, value, elDeviceFamily);
                            conditions.put(contextClaim, DeviceFamiliesEvaluator.Evaluator.getByExpectedValue(value));
                        }
                    }
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            log.error("Exception occurred while reading the device-families definition", e);
        }
    }

    @ToString
    abstract static class Evaluator<T> {

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
