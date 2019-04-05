package com.sdl.webapp.common.api.mapping.semantic.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public final class SemanticVocabulary {

    public static final String SDL_CORE = "http://www.sdl.com/web/schemas/core";

    /**
     * The internal/built-in Vocabulary ID used for DITA mapping.
     */
    public static final String SDL_DITA = "DitaVocabulary";

    public static final String SCHEMA_ORG = "http://schema.org/";

    public static final SemanticVocabulary SDL_CORE_VOCABULARY = new SemanticVocabulary(SDL_CORE);

    public static final SemanticVocabulary SDL_DITA_VOCABULARY = new SemanticVocabulary(SDL_DITA);

    public static final SemanticVocabulary SCHEMA_ORG_VOCABULARY = new SemanticVocabulary(SCHEMA_ORG);

    @Getter
    private final String id;

    @Getter
    @Setter
    private String prefix;

    @Getter
    @Setter
    private String vocab;

    public SemanticVocabulary(String id) {
        this.id = id;
    }
}
