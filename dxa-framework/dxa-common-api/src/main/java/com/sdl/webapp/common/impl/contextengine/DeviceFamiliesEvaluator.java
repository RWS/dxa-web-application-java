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

import static com.sdl.webapp.common.api.contextengine.ContextClaims.castClaim;

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
            for (Map.Entry<String, Map<String, Evaluator>> family : deviceFamiliesRules.entrySet()) {
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
                               @NonNull Map<String, Evaluator> families) {
        for (Map.Entry<String, Evaluator> conditions : families.entrySet()) {
            Evaluator evaluator = conditions.getValue();
            String claimName = conditions.getKey();

            if (!claims.containsKey(claimName) ||
                    !evaluator.evaluate(castClaim(claims.get(claimName), evaluator.genericType()))) {
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
                            conditions.put(contextClaim, Evaluator.getByExpectedValue(value));
                        }
                    }
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            log.error("Exception occurred while reading the device-families definition", e);
        }
    }

    /**
     * Evaluator for device families conditions.
     *
     * @param <T> type of evaluation
     * @since 1.5
     */
    @ToString
    abstract static class Evaluator<T> {

        T expected;

        Sign sign;

        Evaluator(T expected, Sign sign) {
            this.expected = expected;
            this.sign = sign;
        }

        static Evaluator getByExpectedValue(final String conditionValue) {
            if (isBooleanCondition(conditionValue)) {
                return new Evaluator<Boolean>(Boolean.parseBoolean(conditionValue), Sign.EQUAL) {
                    @Override
                    boolean evaluate(Boolean value) {
                        return Objects.equals(expected, value);
                    }

                    @Override
                    Class<Boolean> genericType() {
                        return Boolean.class;
                    }
                };
            }

            //if is an integer condition
            Pattern pattern = Pattern.compile("([<>])(\\d+)");
            final Matcher matcher = pattern.matcher(conditionValue);
            if (matcher.matches() && matcher.groupCount() == 2) {
                final String signSymbol = matcher.group(1);
                final String number = matcher.group(2);

                return new Evaluator<Integer>(Integer.parseInt(number), Sign.gtOrLt(signSymbol)) {
                    @Override
                    boolean evaluate(Integer value) {
                        return ">".equalsIgnoreCase(signSymbol) ? value > expected : value < expected;
                    }

                    @Override
                    Class<Integer> genericType() {
                        return Integer.class;
                    }
                };
            }

            //the rest of conditions are compared by String equality
            return new Evaluator<String>(conditionValue, Sign.EQUAL) {
                @Override
                boolean evaluate(String value) {
                    return Objects.equals(value, conditionValue);
                }

                @Override
                Class<String> genericType() {
                    return String.class;
                }
            };
        }

        private static boolean isBooleanCondition(String conditionValue) {
            return "true".equalsIgnoreCase(conditionValue) || "false".equalsIgnoreCase(conditionValue);
        }

        /**
         * Evaluates if the current value is true with a condition defined in this evaluator.
         * <p>NB! Since claims are of class {@link Object}, and their runtime class is unpredictable,
         * the good idea is to cast passed value to a class returned by {@link #genericType()} to prevent
         * {@link ClassCastException} in runtime.</p>
         *
         * @param value value to test against
         * @return true if condition is true, false otherwise
         */
        abstract boolean evaluate(T value);

        /**
         * Generic class that the value should be casted to before calling {@link #evaluate(Object)} in order to prevent
         * {@link ClassCastException} in runtime.
         *
         * @return generic class of a current evaluator
         */
        abstract Class<T> genericType();

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
