package com.sdl.webapp.common.api.mapping.semantic.config;

import java.util.Objects;

/**
 * <p>FieldSemantics class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class FieldSemantics {

    private final SemanticVocabulary vocabulary;

    private final String entityName;

    private final String propertyName;

    /**
     * <p>Constructor for FieldSemantics.</p>
     *
     * @param vocabulary   a {@link com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary} object.
     * @param entityName   a {@link java.lang.String} object.
     * @param propertyName a {@link java.lang.String} object.
     */
    public FieldSemantics(SemanticVocabulary vocabulary, String entityName, String propertyName) {
        this.vocabulary = vocabulary;
        this.entityName = entityName;
        this.propertyName = propertyName;
    }

    /**
     * <p>Getter for the field <code>vocabulary</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary} object.
     */
    public SemanticVocabulary getVocabulary() {
        return vocabulary;
    }

    /**
     * <p>Getter for the field <code>entityName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getEntityName() {
        return entityName;
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
     * <p>isStandardMetadataField.</p>
     *
     * @return a boolean.
     */
    public boolean isStandardMetadataField() {
        return Objects.equals(this.entityName, "StandardMetadata");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldSemantics that = (FieldSemantics) o;
        return Objects.equals(vocabulary, that.vocabulary) &&
                Objects.equals(entityName, that.entityName) &&
                Objects.equals(propertyName, that.propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(vocabulary, entityName, propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "FieldSemantics{" +
                "vocabulary=" + vocabulary +
                ", entityName='" + entityName + '\'' +
                ", propertyName='" + propertyName + '\'' +
                '}';
    }
}
