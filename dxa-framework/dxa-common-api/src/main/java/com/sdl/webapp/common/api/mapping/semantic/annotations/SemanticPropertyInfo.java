package com.sdl.webapp.common.api.mapping.semantic.annotations;

import com.google.common.base.Strings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
public final class SemanticPropertyInfo {

    private final String prefix;

    private final String propertyName;

    /**
     * <p>Constructor for SemanticPropertyInfo.</p>
     *
     * @param annotation a {@link com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty} object.
     * @param field      a {@link java.lang.reflect.Field} object.
     */
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

    /**
     * <p>Constructor for SemanticPropertyInfo.</p>
     *
     * @param field a {@link java.lang.reflect.Field} object.
     */
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
}
