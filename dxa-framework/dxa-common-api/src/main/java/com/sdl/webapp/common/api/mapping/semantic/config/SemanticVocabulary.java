package com.sdl.webapp.common.api.mapping.semantic.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
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

    @Getter
    private final String id;

    @Getter
    @Setter
    private String prefix;

    @Getter
    @Setter
    private String vocab;

    /**
     * <p>Constructor for SemanticVocabulary.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public SemanticVocabulary(String id) {
        this.id = id;
    }
}
