package com.sdl.webapp.common.api.mapping.config;

/**
 * Semantic vocabulary.
 */
public final class SemanticVocabulary {

    public static final String SDL_CORE = "http://www.sdl.com/web/schemas/core";
    public static final String SCHEMA_ORG = "http://schema.org/";

    public static final SemanticVocabulary SDL_CORE_VOCABULARY = new SemanticVocabulary(SDL_CORE);
    public static final SemanticVocabulary SCHEMA_ORG_VOCABULARY = new SemanticVocabulary(SCHEMA_ORG);

    private final String id;

    public SemanticVocabulary(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SemanticVocabulary that = (SemanticVocabulary) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "[" + id + "]";
    }
}
