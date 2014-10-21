package com.sdl.webapp.dd4t.entityfactory;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.impl.ComponentLinkField;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.NumericField;
import org.dd4t.contentmodel.impl.TextField;

import java.util.ArrayList;
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
        final List<String> values = getStringValues(fields, name);
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
        final List<Double> values = getNumericValues(fields, name);
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

    public static List<GenericComponent> getComponentValues(Map<String, Field> fields, String name) {
        if (fields != null) {
            final Field field = fields.get(name);
            if (field != null && field instanceof ComponentLinkField) {
                List<GenericComponent> list = new ArrayList<>();
                for (Component component : ((ComponentLinkField) field).getLinkedComponentValues()) {
                    if (component instanceof GenericComponent) {
                        list.add((GenericComponent) component);
                    }
                }
                return list;
            }
        }
        return Collections.emptyList();
    }

    public static GenericComponent getComponentValue(Map<String, Field> fields, String name) {
        final List<GenericComponent> values = getComponentValues(fields, name);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }
}
