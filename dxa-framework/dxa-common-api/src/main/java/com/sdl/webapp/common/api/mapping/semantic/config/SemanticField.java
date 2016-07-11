package com.sdl.webapp.common.api.mapping.semantic.config;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
public final class SemanticField {

    private final String name;

    private final FieldPath path;

    private final boolean multiValue;

    private final Map<FieldSemantics, SemanticField> embeddedFields;

    /**
     * <p>Constructor for SemanticField.</p>
     *
     * @param name           a {@link java.lang.String} object.
     * @param path           a {@link java.lang.String} object.
     * @param multiValue     a boolean.
     * @param embeddedFields a {@link java.util.Map} object.
     */
    public SemanticField(String name, String path, boolean multiValue,
                         Map<FieldSemantics, SemanticField> embeddedFields) {
        this.name = name;
        this.path = new FieldPath(path);
        this.multiValue = multiValue;
        this.embeddedFields = ImmutableMap.copyOf(embeddedFields);
    }
}
