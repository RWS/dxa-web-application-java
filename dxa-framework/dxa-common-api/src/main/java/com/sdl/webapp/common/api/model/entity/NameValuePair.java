package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

@SemanticEntity(entityName = "NameValuePair", vocabulary = SDL_CORE, prefix = "nv")
@Data
@EqualsAndHashCode()
public class NameValuePair {

    @SemanticProperty("nv:name")
    @JsonProperty("Name")
    private String name;

    @SemanticProperty("nv:value")
    @JsonProperty("Value")
    private String value;
}
