package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * <p>JsonSchemaField class.</p>
 */
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

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>path</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPath() {
        return path;
    }

    /**
     * <p>Setter for the field <code>path</code>.</p>
     *
     * @param path a {@link java.lang.String} object.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * <p>isMultiValue.</p>
     *
     * @return a boolean.
     */
    public boolean isMultiValue() {
        return isMultiValue;
    }

    /**
     * <p>setMultiValue.</p>
     *
     * @param isMultiValue a boolean.
     */
    public void setMultiValue(boolean isMultiValue) {
        this.isMultiValue = isMultiValue;
    }

    /**
     * <p>Getter for the field <code>semantics</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<JsonFieldSemantics> getSemantics() {
        return semantics;
    }

    /**
     * <p>Setter for the field <code>semantics</code>.</p>
     *
     * @param semantics a {@link java.util.List} object.
     */
    public void setSemantics(List<JsonFieldSemantics> semantics) {
        this.semantics = semantics;
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
}
