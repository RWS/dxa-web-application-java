package com.sdl.webapp.main.markup;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.MessageFormat;

public final class Formatters {

    private Formatters() {
    }

    public static String formatDateTime(DateTime dateTime, String pattern) {
        return DateTimeFormat.forPattern(pattern).print(dateTime);
    }

    public static String formatMessage(String pattern, String arg) {
        return MessageFormat.format(pattern, arg);
    }
}
