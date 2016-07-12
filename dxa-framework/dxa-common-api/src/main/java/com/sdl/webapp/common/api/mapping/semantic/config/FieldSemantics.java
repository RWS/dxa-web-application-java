package com.sdl.webapp.common.api.mapping.semantic.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
public final class FieldSemantics {

    private final SemanticVocabulary vocabulary;

    private final String entityName;

    private final String propertyName;

    /**
     * <p>Constructor for FieldSemantics.</p>
     *
     * @param vocabulary   a {@link com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary} object.
     * @param entityName   a {@link java.lang.String} object.
     * @param propertyName a {@link java.lang.String} object.
     */
    public FieldSemantics(SemanticVocabulary vocabulary, String entityName, String propertyName) {
        this.vocabulary = vocabulary;
        this.entityName = entityName;
        this.propertyName = propertyName;
    }

    public boolean isStandardMetadataField() {
        return Objects.equals(this.entityName, "StandardMetadata");
    }
}
