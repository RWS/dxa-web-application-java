package com.sdl.webapp.common.impl.mapping;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import java.lang.reflect.Field;

public class SemanticPropertyInfo {

    private final String prefix;
    private final String propertyName;
    private final Field field;

    public SemanticPropertyInfo(String prefix, String propertyName, Field field) {
        this.prefix = prefix;
        this.propertyName = propertyName;
        this.field = field;
    }

    public SemanticPropertyInfo(SemanticProperty annotation, Field field) {
        this.field = field;
        String s = annotation.propertyName();
        if (Strings.isNullOrEmpty(s)) {
            s = Strings.nullToEmpty(annotation.value());
        }

        final int i = s.indexOf(':');

        this.prefix = i > 0 ? s.substring(0, i) : "";
        this.propertyName = i > 0 && s.length() > i + 1 ? s.substring(i + 1) : (i < 0 ? s : "");
    }

    public String getPrefix() {
        return prefix;
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

        if (prefix != null ? !prefix.equals(that.prefix) : that.prefix != null) return false;
        if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = prefix != null ? prefix.hashCode() : 0;
        result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SemanticPropertyInfo{" +
                "prefix='" + prefix + '\'' +
                ", propertyName='" + propertyName + '\'' +
                ", field=" + field +
                '}';
    }
}
