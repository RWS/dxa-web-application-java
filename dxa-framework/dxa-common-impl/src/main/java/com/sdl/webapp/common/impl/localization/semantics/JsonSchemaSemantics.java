package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>JsonSchemaSemantics class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class JsonSchemaSemantics {

    @JsonProperty("Prefix")
    private String prefix;

    @JsonProperty("Entity")
    private String entity;

    /**
     * <p>Getter for the field <code>prefix</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>Setter for the field <code>prefix</code>.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * <p>Getter for the field <code>entity</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getEntity() {
        return entity;
    }

    /**
     * <p>Setter for the field <code>entity</code>.</p>
     *
     * @param entity a {@link java.lang.String} object.
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }
}
