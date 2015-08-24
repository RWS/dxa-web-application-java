package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

/**
 * NameValuePair
 *
 * @author nic
 */
@SemanticEntity(entityName = "NameValuePair", vocabulary = SDL_CORE, prefix = "nv")
public class NameValuePair {

    @SemanticProperty("nv:name")
    private String name;

    @SemanticProperty("nv:value")
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
