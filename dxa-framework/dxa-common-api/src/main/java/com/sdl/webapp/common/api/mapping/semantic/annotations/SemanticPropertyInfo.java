package com.sdl.webapp.common.api.mapping.semantic.annotations;

import com.google.common.base.Strings;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * <p>SemanticPropertyInfo class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
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

    /**
     * <p>Getter for the field <code>prefix</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>Getter for the field <code>propertyName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemanticPropertyInfo that = (SemanticPropertyInfo) o;
        return Objects.equals(prefix, that.prefix) &&
                Objects.equals(propertyName, that.propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(prefix, propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "SemanticPropertyInfo{" +
                "prefix='" + prefix + '\'' +
                ", propertyName='" + propertyName + '\'' +
                '}';
    }
}
