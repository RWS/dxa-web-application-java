package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

/**
 * NameValuePair
 *
 * @author nic
 */
@SemanticEntity(entityName = "NameValuePair", vocabulary = SDL_CORE, prefix = "nv")
public class NameValuePair {

    @SemanticProperty("nv:name")
    @JsonProperty("Name")
    private String name;

    @SemanticProperty("nv:value")
    @JsonProperty("Value")
    private String value;

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getValue() {
        return value;
    }
}
