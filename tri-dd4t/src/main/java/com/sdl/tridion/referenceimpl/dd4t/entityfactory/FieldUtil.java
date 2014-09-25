package com.sdl.tridion.referenceimpl.dd4t.entityfactory;

import org.dd4t.contentmodel.Field;

import java.util.List;
import java.util.Map;

public final class FieldUtil {

    private FieldUtil() {
    }

    public static String getFieldStringValue(Map<String, Field> fields, String name) {
        if (fields != null) {
            final Field field = fields.get(name);
            if (field != null) {
                final List<Object> values = field.getValues();
                if (values != null && !values.isEmpty()) {
                    return (String) values.get(0);
                }
            }
        }

        return null;
    }
}
