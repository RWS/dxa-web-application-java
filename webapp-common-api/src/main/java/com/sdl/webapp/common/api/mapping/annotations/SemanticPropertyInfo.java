package com.sdl.webapp.common.api.mapping.annotations;

import com.google.common.base.Strings;

import java.lang.reflect.Field;
import java.util.List;

public final class SemanticPropertyInfo {

    private final String prefix;

    private final String propertyName;

    public SemanticPropertyInfo(SemanticProperty annotation, Field field) {
        String n = annotation.propertyName();
        if (Strings.isNullOrEmpty(n)) {
            n = annotation.value();
        }

        final int i = n.indexOf(':');
        if (i < 0) {
            this.prefix = SemanticEntityInfo.DEFAULT_PREFIX;
        } else {
            this.prefix = n.substring(0, i);
            n = n.substring(i + 1);
        }

        if (Strings.isNullOrEmpty(n)) {
            n = getDefaultPropertyName(field);
        }
        this.propertyName = n;
    }

    public SemanticPropertyInfo(Field field) {
        this.prefix = SemanticEntityInfo.DEFAULT_PREFIX;
        this.propertyName = getDefaultPropertyName(field);
    }

    private static String getDefaultPropertyName(Field field) {
        String propertyName = field.getName();

        // Special handling: when the field is a List and the name ends with "s", then remove the "s"
        // (so that for example "paragraphs" becomes "paragraph")
        if (List.class.isAssignableFrom(field.getType()) && propertyName.endsWith("s")) {
            propertyName = propertyName.substring(0, propertyName.length() - 1);
        }

        return propertyName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String toString() {
        return "SemanticPropertyInfo{" +
                "prefix='" + prefix + '\'' +
                ", propertyName='" + propertyName + '\'' +
                '}';
    }
}
