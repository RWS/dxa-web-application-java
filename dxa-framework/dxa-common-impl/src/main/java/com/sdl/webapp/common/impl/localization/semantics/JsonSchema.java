package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * <p>JsonSchema class.</p>
 */
public class JsonSchema {

    @JsonProperty("Id")
    private long id;

    @JsonProperty("RootElement")
    private String rootElement;

    @JsonProperty("Fields")
    private List<JsonSchemaField> fields;

    @JsonProperty("Semantics")
    private List<JsonSchemaSemantics> semantics;

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a long.
     */
    public long getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a long.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>rootElement</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRootElement() {
        return rootElement;
    }

    /**
     * <p>Setter for the field <code>rootElement</code>.</p>
     *
     * @param rootElement a {@link java.lang.String} object.
     */
    public void setRootElement(String rootElement) {
        this.rootElement = rootElement;
    }

    /**
     * <p>Getter for the field <code>fields</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<JsonSchemaField> getFields() {
        return fields;
    }

    /**
     * <p>Setter for the field <code>fields</code>.</p>
     *
     * @param fields a {@link java.util.List} object.
     */
    public void setFields(List<JsonSchemaField> fields) {
        this.fields = fields;
    }

    /**
     * <p>Getter for the field <code>semantics</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<JsonSchemaSemantics> getSemantics() {
        return semantics;
    }

    /**
     * <p>Setter for the field <code>semantics</code>.</p>
     *
     * @param semantics a {@link java.util.List} object.
     */
    public void setSemantics(List<JsonSchemaSemantics> semantics) {
        this.semantics = semantics;
    }
}
