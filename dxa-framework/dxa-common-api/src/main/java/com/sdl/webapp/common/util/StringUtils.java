package com.sdl.webapp.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static org.springframework.util.StringUtils.isEmpty;

public final class StringUtils {

    /**
     * Replaces {0}-like placeholders in the string to format to Java's %1$s preserving the order of elements.
     * @param cmFormatString format string with {0}, {1}, ... as placeholders
     * @return same format string with replaced {0}... to %1$s
     */
    public static String convertFormatStringFromCM(String cmFormatString) {
        if (isEmpty(cmFormatString)) {
            return cmFormatString;
        }

        StringBuffer out = new StringBuffer();
        Pattern pattern = Pattern.compile("\\{(\\d)\\}");
        Matcher matcher = pattern.matcher(cmFormatString);
        while (matcher.find()) {
            String replacement = String.format("%%%d\\$s", parseInt(matcher.group().replaceAll("[\\{\\}]", "")) + 1);
            matcher.appendReplacement(out, replacement);
        }
        matcher.appendTail(out);
        return out.toString();
    }
}
