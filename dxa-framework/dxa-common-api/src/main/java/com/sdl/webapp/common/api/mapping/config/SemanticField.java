package com.sdl.webapp.common.api.mapping.config;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Semantic field.
 */
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

        if (multiValue != that.multiValue) return false;
        if (embeddedFields != null ? !embeddedFields.equals(that.embeddedFields) : that.embeddedFields != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (multiValue ? 1 : 0);
        result = 31 * result + (embeddedFields != null ? embeddedFields.hashCode() : 0);
        return result;
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
