package com.sdl.webapp.common.util;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * @dxa.publicApi
 */
public final class StringUtils {

    /**
     * Replaces {0}-like placeholders in the string to format to Java's %1$s preserving the order of elements.
     *
     * @param cmFormatString format string with {0}, {1}, ... as placeholders
     * @return same format string with replaced {0}... to %1$s
     */
    public static String convertFormatStringFromCM(String cmFormatString) {
        if (isEmpty(cmFormatString)) {
            return cmFormatString;
        }

        StringBuffer out = new StringBuffer();
        Pattern pattern = Pattern.compile("\\{(\\d)}");
        Matcher matcher = pattern.matcher(cmFormatString);
        while (matcher.find()) {
            String replacement = String.format("%%%d\\$s", parseInt(matcher.group().replaceAll("[{}]", "")) + 1);
            matcher.appendReplacement(out, replacement);
        }
        matcher.appendTail(out);
        return out.toString();
    }


    /**
     * Transforms the list of any objects to the {@linkplain List list} of {@linkplain String strings}.
     * Please note that {@code null} value is interpreted as {@code empty} value.
     *
     * @param list list to transform
     * @return list of strings
     */
    public static List<String> toStrings(List<?> list) {
        return Lists.transform(list, (Function<Object, String>) input -> {
            if (input == null) {
                return "";
            }
            if (input instanceof String) {
                return (String) input;
            }
            return input.toString();
        });
    }

    /**
     * Replaces spaces with dashes.
     *
     * @param string string to process
     * @return string with spaces replaced or {@code null} is string is null
     */
    @Contract("null -> null; !null -> !null")
    public static String dashify(@Nullable String string) {
        return string == null ? null : string.replace(" ", "-");
    }
}
