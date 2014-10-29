package com.sdl.webapp.common.api.mapping.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Schema semantics.
 */
public class SchemaSemantics {

    @JsonProperty("Prefix")
    private String prefix;

    @JsonProperty("Entity")
    private String entityName;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public String toString() {
        return "SchemaSemantics{" +
                "prefix='" + prefix + '\'' +
                ", entityName='" + entityName + '\'' +
                '}';
    }
}
