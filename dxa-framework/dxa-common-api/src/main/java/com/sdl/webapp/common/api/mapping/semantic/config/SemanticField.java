package com.sdl.webapp.common.api.mapping.semantic.config;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
@Slf4j
public final class SemanticField implements WithXPath {

    private final String name;

    private final FieldPath path;

    private final boolean multiValue;

    private final Map<FieldSemantics, SemanticField> embeddedFields;

    public SemanticField(String name, String path, boolean multiValue,
                         Map<FieldSemantics, SemanticField> embeddedFields) {
        this.name = name;
        this.path = new FieldPath(path);
        this.multiValue = multiValue;
        this.embeddedFields = ImmutableMap.copyOf(embeddedFields);
    }

    @Override
    @NotNull
    public String getXPath(@Nullable String contextXPath) {
        return path.getXPath(contextXPath);
    }
}
