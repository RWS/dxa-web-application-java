package com.sdl.webapp.common.api.mapping.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Semantic schema field.
 */
public class SemanticSchemaField {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Path")
    private String path;

    @JsonProperty("IsMultiValue")
    private boolean isMultiValue;

    @JsonProperty("Semantics")
    private List<FieldSemantics> semantics;

    @JsonProperty("Fields")
    private List<SemanticSchemaField> fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isMultiValue() {
        return isMultiValue;
    }

    public void setMultiValue(boolean isMultiValue) {
        this.isMultiValue = isMultiValue;
    }

    public List<FieldSemantics> getSemantics() {
        return semantics;
    }

    public void setSemantics(List<FieldSemantics> semantics) {
        this.semantics = semantics;
    }

    public List<SemanticSchemaField> getFields() {
        return fields;
    }

    public void setFields(List<SemanticSchemaField> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "SemanticSchemaField{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", isMultiValue=" + isMultiValue +
                ", semantics=" + semantics +
                ", fields=" + fields +
                '}';
    }
}
