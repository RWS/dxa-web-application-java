package com.sdl.webapp.common.api.mapping.semantic.config;

import java.util.Objects;

/**
 * <p>SemanticVocabulary class.</p>
 */
public final class SemanticVocabulary {

    /**
     * Constant <code>SDL_CORE="http://www.sdl.com/web/schemas/core"</code>
     */
    public static final String SDL_CORE = "http://www.sdl.com/web/schemas/core";
    /**
     * Constant <code>SCHEMA_ORG="http://schema.org/"</code>
     */
    public static final String SCHEMA_ORG = "http://schema.org/";

    /** Constant <code>SDL_CORE_VOCABULARY</code> */
    public static final SemanticVocabulary SDL_CORE_VOCABULARY = new SemanticVocabulary(SDL_CORE);
    /** Constant <code>SCHEMA_ORG_VOCABULARY</code> */
    public static final SemanticVocabulary SCHEMA_ORG_VOCABULARY = new SemanticVocabulary(SCHEMA_ORG);

    private final String id;
    private String prefix;
    private String vocab;

    /**
     * <p>Constructor for SemanticVocabulary.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public SemanticVocabulary(String id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return id;
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
     * <p>Setter for the field <code>prefix</code>.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * <p>Getter for the field <code>vocab</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVocab() {
        return vocab;
    }

    /**
     * <p>Setter for the field <code>vocab</code>.</p>
     *
     * @param vocab a {@link java.lang.String} object.
     */
    public void setVocab(String vocab) {
        this.vocab = vocab;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemanticVocabulary that = (SemanticVocabulary) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(prefix, that.prefix) &&
                Objects.equals(vocab, that.vocab);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(id, prefix, vocab);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return '[' + id + ']';
    }
}
