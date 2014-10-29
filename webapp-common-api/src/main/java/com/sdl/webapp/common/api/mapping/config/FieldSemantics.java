package com.sdl.webapp.common.api.mapping.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Field semantics.
 */
public class FieldSemantics {

    @JsonProperty("Prefix")
    private String prefix;

    @JsonProperty("Entity")
    private String entityName;

    @JsonProperty("Property")
    private String propertyName;

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

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return "FieldSemantics{" +
                "prefix='" + prefix + '\'' +
                ", entityName='" + entityName + '\'' +
                ", propertyName='" + propertyName + '\'' +
                '}';
    }
}
