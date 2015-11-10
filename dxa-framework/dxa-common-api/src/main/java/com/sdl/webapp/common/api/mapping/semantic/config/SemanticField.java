package com.sdl.webapp.common.api.mapping.semantic.config;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

public final class SemanticField {

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

    public String getName() {
        return name;
    }

    public FieldPath getPath() {
        return path;
    }

    public boolean isMultiValue() {
        return multiValue;
    }

    public Map<FieldSemantics, SemanticField> getEmbeddedFields() {
        return embeddedFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemanticField that = (SemanticField) o;
        return Objects.equals(multiValue, that.multiValue) &&
                Objects.equals(name, that.name) &&
                Objects.equals(path, that.path) &&
                Objects.equals(embeddedFields, that.embeddedFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, multiValue, embeddedFields);
    }

    @Override
    public String toString() {
        return "SemanticField{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", multiValue=" + multiValue +
                ", embeddedFields=" + embeddedFields +
                '}';
    }
}
