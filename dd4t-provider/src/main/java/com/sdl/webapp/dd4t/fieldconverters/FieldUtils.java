package com.sdl.webapp.dd4t.fieldconverters;

import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.impl.BaseField;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class FieldUtils {

    private FieldUtils() {
    }

    public static List<String> getStringValues(Field field) {
        if (field != null) {
            final List<String> values = ((BaseField) field).getTextValues();
            return values != null ? values : Collections.<String>emptyList();
        }
        return Collections.emptyList();
    }

    public static String getStringValue(Field field) {
        final List<String> values = getStringValues(field);
        return !values.isEmpty() ? values.get(0) : null;
    }

    public static String getStringValue(Map<String, Field> fields, String name) {
        return fields != null ? getStringValue(fields.get(name)) : null;
    }
}
