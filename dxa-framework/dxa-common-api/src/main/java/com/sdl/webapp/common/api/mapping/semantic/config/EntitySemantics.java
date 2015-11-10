package com.sdl.webapp.common.api.mapping.semantic.config;

import java.util.Objects;

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
        return Objects.equals(vocabulary, that.vocabulary) &&
                Objects.equals(entityName, that.entityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vocabulary, entityName);
    }

    @Override
    public String toString() {
        return "EntitySemantics{" +
                "vocabulary=" + vocabulary +
                ", entityName='" + entityName + '\'' +
                '}';
    }
}
