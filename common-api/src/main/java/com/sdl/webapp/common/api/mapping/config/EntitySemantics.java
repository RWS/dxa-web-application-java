package com.sdl.webapp.common.api.mapping.config;

/**
 * Entity semantics.
 */
public final class EntitySemantics {

    private final SemanticVocabulary vocabulary;

    private final String entityName;

    public EntitySemantics(SemanticVocabulary vocabulary, String entityName) {
        this.vocabulary = vocabulary;
        this.entityName = entityName;
    }

    public SemanticVocabulary getVocabulary() {
        return vocabulary;
    }

    public String getEntityName() {
        return entityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntitySemantics that = (EntitySemantics) o;

        if (entityName != null ? !entityName.equals(that.entityName) : that.entityName != null) return false;
        if (vocabulary != null ? !vocabulary.equals(that.vocabulary) : that.vocabulary != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = vocabulary != null ? vocabulary.hashCode() : 0;
        result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EntitySemantics{" +
                "vocabulary=" + vocabulary +
                ", entityName='" + entityName + '\'' +
                '}';
    }
}
