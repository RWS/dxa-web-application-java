package com.sdl.webapp.common.api.mapping.semantic.config;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

/**
 * <p>SemanticField class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class SemanticField {

    private final String name;

    private final FieldPath path;

    private final boolean multiValue;

    private final Map<FieldSemantics, SemanticField> embeddedFields;

    /**
     * <p>Constructor for SemanticField.</p>
     *
     * @param name           a {@link java.lang.String} object.
     * @param path           a {@link java.lang.String} object.
     * @param multiValue     a boolean.
     * @param embeddedFields a {@link java.util.Map} object.
     */
    public SemanticField(String name, String path, boolean multiValue,
                         Map<FieldSemantics, SemanticField> embeddedFields) {
        this.name = name;
        this.path = new FieldPath(path);
        this.multiValue = multiValue;
        this.embeddedFields = ImmutableMap.copyOf(embeddedFields);
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Getter for the field <code>path</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.mapping.semantic.config.FieldPath} object.
     */
    public FieldPath getPath() {
        return path;
    }

    /**
     * <p>isMultiValue.</p>
     *
     * @return a boolean.
     */
    public boolean isMultiValue() {
        return multiValue;
    }

    /**
     * <p>Getter for the field <code>embeddedFields</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<FieldSemantics, SemanticField> getEmbeddedFields() {
        return embeddedFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemanticField that = (SemanticField) o;
        return Objects.equals(multiValue, that.multiValue) &&
                Objects.equals(name, that.name) &&
                Objects.equals(path, that.path) &&
                Objects.equals(embeddedFields, that.embeddedFields);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(name, path, multiValue, embeddedFields);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "SemanticField{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", multiValue=" + multiValue +
                ", embeddedFields=" + embeddedFields +
                '}';
    }
}
