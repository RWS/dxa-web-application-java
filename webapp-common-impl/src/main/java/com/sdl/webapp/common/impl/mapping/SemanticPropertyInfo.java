package com.sdl.webapp.common.impl.mapping;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import java.lang.reflect.Field;

/**
 * Semantic information for a field of an entity class.
 */
final class SemanticPropertyInfo {

    private final String propertyName;
    private final Field field;

    public SemanticPropertyInfo(String propertyName, Field field) {
        this.propertyName = propertyName;
        this.field = field;
    }

    public SemanticPropertyInfo(SemanticProperty annotation, Field field) {
        String s = annotation.propertyName();
        if (Strings.isNullOrEmpty(s)) {
            s = Strings.nullToEmpty(annotation.value());
        }
        if (Strings.isNullOrEmpty(s)) {
            s = field.getName();
        }
        final int i = s.indexOf(':');

        this.propertyName = s.length() > i + 1 ? s.substring(i + 1) : field.getName();
        this.field = field;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Field getField() {
        return field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SemanticPropertyInfo that = (SemanticPropertyInfo) o;

        if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return propertyName != null ? propertyName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SemanticPropertyInfo{" +
                "propertyName='" + propertyName + '\'' +
                ", field=" + field +
                '}';
    }
}
