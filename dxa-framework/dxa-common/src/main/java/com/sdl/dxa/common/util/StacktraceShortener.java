package com.sdl.dxa.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StacktraceShortener {
    private static final int DEFAULT_LENGTH = 512;
    private final List<ProcessorRule> allRulesToCollapse = Arrays.asList(new ProcessorRule[] {
            new ProcessorRule("-- REFLECTION", new String[] {"java.lang.reflect.", "sun.reflect."}),
            new ProcessorRule("-- TOMCAT", new String[]{"org.apache.catalina.", "org.apache.coyote.", "org.apache.tomcat."}),
            new ProcessorRule("-- WEBSPHERE", new String[] {"com.ibm.ws.", "com.ibm.websphere."}),
            new ProcessorRule("-- SPRING", "org.springframework."),
            new ProcessorRule("-- FREEMARKER CACHE", "freemarker.cache."),
            new ProcessorRule("-- JACKSON", "com.fasterxml.jackson."),
            new ProcessorRule("-- ACTIVEMQ", "org.apache.activemq."),
            new ProcessorRule("-- HIBERNATE", "org.hibernate."),
            new ProcessorRule("-- MS SQL driver", "com.microsoft.sqlserver."),
            new ProcessorRule("-- MySQL driver", "com.mysql."),
            new ProcessorRule("-- Oracle driver", "oracle.jdbc."),
    });

    private final List<ProcessorRule> allRulesToLeftExpanded = new ArrayList<>();

    private Throwable throwable;

    public StacktraceShortener(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Allows to add user defined rule for compacting rows.
     * @param compactedName name of rule - will be visible in stacktrace.
     * @param rule in fact array of texts, you do not want to see expanded in stacktrace.
     */
    public void addRuleToCollapse(String compactedName, String[] rule) {
        allRulesToCollapse.add(new ProcessorRule(compactedName, rule));
    }

    /**
     * Allows to add rules which will always be expanded. Usually it's not necessary,
     * but it can help processing faster.
     * @param ruleName just name of rule, it's not used anywhere.
     * @param rule in fact array of texts, you want to see expanded in stacktracea.
     */
    public void addRuleToBeLeftExpanded(String ruleName, String[] rule) {
        allRulesToLeftExpanded.add(new ProcessorRule(ruleName, rule));
    }

    private static class ProcessorRule {
        private final Set<String> rules;
        private final String compactedName;
        private Boolean prevRuleMet = null;
        private boolean currentRuleMet = false;
        private int counter = 0;

        ProcessorRule(String compactedName, String rule) {
            this.rules = new HashSet<>();
            rules.add(rule);
            this.compactedName = compactedName;
        }

        ProcessorRule(String compactedName, String[] rules) {
            this.rules = new HashSet<>();
            Arrays.asList(rules).forEach(this.rules::add);
            this.compactedName = compactedName;
        }

        void processCurrentLine(String className) {
            currentRuleMet = rules.stream().anyMatch(className::contains);
        }

        void appendCompacted(StringBuilder result) {
            result.append("\t   ").append(compactedName);
            setPrevious(false);
        }

        void setPrevious(boolean state) {
            prevRuleMet = state;
        }

        boolean isPreviousLineMeetRule() {
            return prevRuleMet != null && prevRuleMet;
        }

        @Override
        public String toString() {
            return "ProcessorRule{" +
                    "" + compactedName +
                    '}';
        }
    }

    public String generateString() {
        if (throwable == null) {
            return "No exception provided";
        }
        if (throwable.getStackTrace() == null) {
            return "No stacktrace provided";
        }
        if (throwable.getStackTrace().length == 0) {
            return "No any stacktrace element provided";
        }
        StringBuilder result = new StringBuilder(DEFAULT_LENGTH);
        result.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            if (elementShouldBeExpanded(element)) {
                result.append("\tat ").append(element.toString()).append("\n");
                continue;
            }
            boolean shouldSkip = false;
            ProcessorRule workedRule = null;
            for (ProcessorRule rule : allRulesToCollapse) {
                if (processRule(result, rule, element)) {
                    shouldSkip = true;
                    workedRule = rule;
                    workedRule.counter++;
                    break;
                }
            }
            if (shouldSkip) {
                for (ProcessorRule rule : allRulesToCollapse) {
                    if (rule == workedRule) {
                        continue;
                    }
                    rule.prevRuleMet = null;
                    rule.currentRuleMet = false;
                    rule.counter = 0;
                }
                continue;
            }
            result.append("\tat ").append(element.toString()).append("\n");
        }
        if (throwable.getCause() != null) {
            StacktraceShortener innerShortener = new StacktraceShortener(throwable.getCause());
            result.append("Caused by: ").append(innerShortener.generateString());
        }
        return result.toString();
    }

    private boolean elementShouldBeExpanded(StackTraceElement element) {
        for (ProcessorRule ruleToExpand : allRulesToLeftExpanded) {
            for (String rule : ruleToExpand.rules) {
                if (element.toString().contains(rule)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean processRule(StringBuilder result, ProcessorRule processorRule, StackTraceElement element) {
        processorRule.processCurrentLine(element.getClassName());
        if (processorRule.currentRuleMet && processorRule.prevRuleMet == null) {
            if (!result.toString().endsWith(processorRule.compactedName)) {
                processorRule.appendCompacted(result);
            }
            processorRule.setPrevious(true);
            return true;
        }
        if (processorRule.currentRuleMet && processorRule.isPreviousLineMeetRule()) {
            //compact
            return true;
        }
        if (processorRule.currentRuleMet || !processorRule.isPreviousLineMeetRule()) {
            if (processorRule.currentRuleMet) {
                processorRule.setPrevious(true);
                return true;
            }
        }
        if (processorRule.prevRuleMet != null && processorRule.prevRuleMet) {
            if (processorRule.counter > 1) {
                result.append(" <" + processorRule.counter + " lines>\n");
            } else if (processorRule.counter == 1) {
                result.append("\n");
            }
        }
        return false;
    }
}
