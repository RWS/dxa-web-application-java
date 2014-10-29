package com.sdl.webapp.common.impl.mapping;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import java.lang.reflect.Field;

final class SemanticPropertyInfo {

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
        // TODO: Special magic, remove 's' from the end, see [C#] BaseModelBuilder.GetDefaultPropertySemantics
        return field.getName();
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
