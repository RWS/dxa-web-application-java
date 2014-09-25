package com.sdl.tridion.referenceimpl.dd4t.entityfactory;

import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.NumericField;
import org.dd4t.contentmodel.impl.TextField;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class FieldUtil {

    private FieldUtil() {
    }

    public static List<String> getStringValues(Map<String, Field> fields, String name) {
        if (fields != null) {
            final Field field = fields.get(name);
            if (field != null && field instanceof TextField) {
                return ((TextField) field).getTextValues();
            }
        }
        return Collections.emptyList();
    }

    public static String getStringValue(Map<String, Field> fields, String name) {
        List<String> values = getStringValues(fields, name);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    public static List<Double> getNumericValues(Map<String, Field> fields, String name) {
        if (fields != null) {
            final Field field = fields.get(name);
            if (field != null && field instanceof NumericField) {
                return ((NumericField) field).getNumericValues();
            }
        }
        return Collections.emptyList();
    }

    public static Double getNumericValue(Map<String, Field> fields, String name) {
        List<Double> values = getNumericValues(fields, name);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    public static int getIntValue(Map<String, Field> fields, String name, int defaultValue) {
        final Double value = getNumericValue(fields, name);
        return value != null ? value.intValue() : defaultValue;
    }

    public static List<FieldSet> getEmbeddedValues(Map<String, Field> fields, String name) {
        if (fields != null) {
            final Field field = fields.get(name);
            if (field != null && field instanceof EmbeddedField) {
                return ((EmbeddedField) field).getEmbeddedValues();
            }
        }
        return Collections.emptyList();
    }
}
