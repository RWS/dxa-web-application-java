package com.sdl.webapp.common.api.mapping.semantic.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public final class EntitySemantics {

    private final SemanticVocabulary vocabulary;

    private final String entityName;

    public EntitySemantics(SemanticVocabulary vocabulary, String entityName) {
        this.vocabulary = vocabulary;
        this.entityName = entityName;
    }
}
