package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class JsonSchemaField {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Path")
    private String path;

    @JsonProperty("IsMultiValue")
    private boolean isMultiValue;

    @JsonProperty("Semantics")
    private List<JsonFieldSemantics> semantics;

    @JsonProperty("Fields")
    private List<JsonSchemaField> fields;

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

    public List<JsonFieldSemantics> getSemantics() {
        return semantics;
    }

    public void setSemantics(List<JsonFieldSemantics> semantics) {
        this.semantics = semantics;
    }

    public List<JsonSchemaField> getFields() {
        return fields;
    }

    public void setFields(List<JsonSchemaField> fields) {
        this.fields = fields;
    }
}
