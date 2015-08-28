package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class JsonSchema {

    @JsonProperty("Id")
    private long id;

    @JsonProperty("RootElement")
    private String rootElement;

    @JsonProperty("Fields")
    private List<JsonSchemaField> fields;

    @JsonProperty("Semantics")
    private List<JsonSchemaSemantics> semantics;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRootElement() {
        return rootElement;
    }

    public void setRootElement(String rootElement) {
        this.rootElement = rootElement;
    }

    public List<JsonSchemaField> getFields() {
        return fields;
    }

    public void setFields(List<JsonSchemaField> fields) {
        this.fields = fields;
    }

    public List<JsonSchemaSemantics> getSemantics() {
        return semantics;
    }

    public void setSemantics(List<JsonSchemaSemantics> semantics) {
        this.semantics = semantics;
    }
}
