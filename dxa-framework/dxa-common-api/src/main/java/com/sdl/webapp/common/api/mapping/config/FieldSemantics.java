package com.sdl.webapp.common.api.mapping.config;

import java.util.Objects;

public final class FieldSemantics {

    private final SemanticVocabulary vocabulary;

    private final String entityName;

    private final String propertyName;

    public FieldSemantics(SemanticVocabulary vocabulary, String entityName, String propertyName) {
        this.vocabulary = vocabulary;
        this.entityName = entityName;
        this.propertyName = propertyName;
    }

    public SemanticVocabulary getVocabulary() {
        return vocabulary;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldSemantics that = (FieldSemantics) o;
        return Objects.equals(vocabulary, that.vocabulary) &&
                Objects.equals(entityName, that.entityName) &&
                Objects.equals(propertyName, that.propertyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vocabulary, entityName, propertyName);
    }

    @Override
    public String toString() {
        return "FieldSemantics{" +
                "vocabulary=" + vocabulary +
                ", entityName='" + entityName + '\'' +
                ", propertyName='" + propertyName + '\'' +
                '}';
    }
}
