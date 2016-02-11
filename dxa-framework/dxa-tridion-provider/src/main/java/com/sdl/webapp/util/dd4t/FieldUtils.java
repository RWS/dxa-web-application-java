package com.sdl.webapp.util.dd4t;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.contentmodel.impl.BaseField;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * <p>FieldUtils class.</p>
 */
public final class FieldUtils {

    /**
     * Constant <code>DOUBLE_TO_STRING_FUNCTION</code>
     */
    public static final Function<Double, String> DOUBLE_TO_STRING_FUNCTION = new Function<Double, String>() {
        @Override
        public String apply(Double input) {
            return Double.toString(input);
        }
    };

    /**
     * Constant <code>COMPONENT_TO_STRING_FUNCTION</code>
     */
    public static final Function<Component, String> COMPONENT_TO_STRING_FUNCTION = new Function<Component, String>() {
        @Override
        public String apply(Component input) {
            return input.getId();
        }
    };

    /** Constant <code>KEYWORD_TO_STRING_FUNCTION</code> */
    public static final Function<Keyword, String> KEYWORD_TO_STRING_FUNCTION = new Function<Keyword, String>() {
        @Override
        public String apply(Keyword input) {
            return input.getId();
        }
    };

    private FieldUtils() {
    }

    /**
     * <p>getStringValue.</p>
     *
     * @param field a {@link org.dd4t.contentmodel.Field} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getStringValue(Field field) {
        final List<String> values = getStringValues(field);
        return !values.isEmpty() ? values.get(0) : null;
    }

    /**
     * <p>getStringValue.</p>
     *
     * @param fields a {@link java.util.Map} object.
     * @param name a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getStringValue(Map<String, Field> fields, String name) {
        return fields != null ? getStringValue(fields.get(name)) : null;
    }

    private static List<String> getStringValues(Field field) {
        if (field != null) {
            BaseField baseField = (BaseField) field;

            final List<String> strings = selectNotEmptyListOrNull(baseField, String.class);
            if (strings != null) {
                return strings;
            }

            final List<Double> doubles = selectNotEmptyListOrNull(baseField, Double.class);
            if (doubles != null) {
                return Lists.transform(doubles, DOUBLE_TO_STRING_FUNCTION);
            }

            final List<Component> components = selectNotEmptyListOrNull(baseField, Component.class);
            if (components != null) {
                return Lists.transform(components, COMPONENT_TO_STRING_FUNCTION);
            }

            final List<Keyword> keywords = selectNotEmptyListOrNull(baseField, Keyword.class);
            if (keywords != null) {
                return Lists.transform(keywords, KEYWORD_TO_STRING_FUNCTION);
            }
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    // suppressed unchecked casts, please ensure that your cast is valid
    private static <T> List<T> selectNotEmptyListOrNull(BaseField baseField, Class<T> clazz) {
        if (String.class.equals(clazz)) {
            return (List<T>) selectNotEmptyListOfStringsOrNull(baseField);
        }
        if (Double.class.equals(clazz)) {
            if(!isEmpty(baseField.getNumericValues())) {
                return (List<T>) baseField.getNumericValues();
            }
        }
        if (Component.class.equals(clazz)) {
            if (!isEmpty(baseField.getLinkedComponentValues())) {
                return (List<T>) baseField.getLinkedComponentValues();
            }
        }
        if (Keyword.class.equals(clazz)) {
            if (!isEmpty(baseField.getKeywordValues())) {
                return (List<T>) baseField.getKeywordValues();
            }
        }
        return null;
    }

    private static List<String> selectNotEmptyListOfStringsOrNull(BaseField baseField) {
        if(!isEmpty(baseField.getTextValues())) {
            return baseField.getTextValues();
        }
        if(!isEmpty(baseField.getDateTimeValues())) {
            return baseField.getDateTimeValues();
        }
        return null;
    }
}
