package com.sdl.tridion.referenceimpl.dd4t.entityfactory;

import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.impl.NumericField;
import org.dd4t.contentmodel.impl.TextField;

import java.util.List;
import java.util.Map;

import static org.dd4t.contentmodel.Field.FieldType;

public final class FieldUtil {

    private FieldUtil() {
    }

    public static String getFieldStringValue(Map<String, Field> fields, String name) {
        if (fields != null) {
            final Field field = fields.get(name);
            if (field != null && field.getFieldType() == FieldType.Text) {
                final List<String> values = ((TextField) field).getTextValues();
                if (values != null && !values.isEmpty()) {
                    return values.get(0);
                }
            }
        }

        return null;
    }

    public static Integer getFieldIntegerValue(Map<String, Field> fields, String name) {
        if (fields != null) {
            final Field field = fields.get(name);
            if (field != null && field.getFieldType() == FieldType.Number) {
                final List<Double> values = ((NumericField) field).getNumericValues();
                if (values != null && !values.isEmpty()) {
                    return values.get(0).intValue();
                }
            }
        }

        return null;
    }

    public static int getFieldIntValue(Map<String, Field> fields, String name, int defaultValue) {
        final Integer value = getFieldIntegerValue(fields, name);
        return value != null ? value : defaultValue;
    }
}
