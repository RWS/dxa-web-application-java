package com.sdl.webapp.common.api.mapping.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SemanticSchema {

    @JsonProperty("Id")
    private long id;

    @JsonProperty("RootElement")
    private String rootElement;

    @JsonProperty("Fields")
    private List<SemanticSchemaField> fields;

    @JsonProperty("Semantics")
    private List<SchemaSemantics> semantics;

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

    public List<SemanticSchemaField> getFields() {
        return fields;
    }

    public void setFields(List<SemanticSchemaField> fields) {
        this.fields = fields;
    }

    public List<SchemaSemantics> getSemantics() {
        return semantics;
    }

    public void setSemantics(List<SchemaSemantics> semantics) {
        this.semantics = semantics;
    }
}
