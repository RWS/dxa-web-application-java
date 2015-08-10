package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonSchemaSemantics {

    @JsonProperty("Prefix")
    private String prefix;

    @JsonProperty("Entity")
    private String entity;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
