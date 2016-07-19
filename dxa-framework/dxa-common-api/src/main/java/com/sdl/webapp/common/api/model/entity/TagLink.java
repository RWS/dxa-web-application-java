package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

@SemanticEntity(entityName = "SocialLink", vocabulary = SDL_CORE, prefix = "s")
@Data
@EqualsAndHashCode(callSuper = true)
public class TagLink extends AbstractEntityModel {

    @SemanticProperties({
            @SemanticProperty("internalLink"),
            @SemanticProperty("externalLink"),
            @SemanticProperty("s:internalLink"),
            @SemanticProperty("s:externalLink")
    })
    @JsonProperty("Url")
    private String url;

    @SemanticProperty("s:tag")
    @JsonProperty("Tag")
    private Tag tag;
}
