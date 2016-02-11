package com.sdl.webapp.common.api.mapping.semantic.config;

import java.util.Objects;

/**
 * <p>EntitySemantics class.</p>
 */
public final class EntitySemantics {

    private final SemanticVocabulary vocabulary;

    private final String entityName;

    /**
     * <p>Constructor for EntitySemantics.</p>
     *
     * @param vocabulary a {@link com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary} object.
     * @param entityName a {@link java.lang.String} object.
     */
    public EntitySemantics(SemanticVocabulary vocabulary, String entityName) {
        this.vocabulary = vocabulary;
        this.entityName = entityName;
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
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntitySemantics that = (EntitySemantics) o;
        return Objects.equals(vocabulary, that.vocabulary) &&
                Objects.equals(entityName, that.entityName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(vocabulary, entityName);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "EntitySemantics{" +
                "vocabulary=" + vocabulary +
                ", entityName='" + entityName + '\'' +
                '}';
    }
}
