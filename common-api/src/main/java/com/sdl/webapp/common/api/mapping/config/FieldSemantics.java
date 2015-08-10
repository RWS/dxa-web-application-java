package com.sdl.webapp.common.api.mapping.config;

/**
 * Field semantics.
 */
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

        if (entityName != null ? !entityName.equals(that.entityName) : that.entityName != null) return false;
        if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null) return false;
        if (vocabulary != null ? !vocabulary.equals(that.vocabulary) : that.vocabulary != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = vocabulary != null ? vocabulary.hashCode() : 0;
        result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
        result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
        return result;
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
