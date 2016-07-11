package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

@SemanticEntity(entityName = "NotificationBar", vocabulary = SDL_CORE, prefix = "nb")
@Data
@EqualsAndHashCode(callSuper = true)
public class Notification extends AbstractEntityModel {

    @SemanticProperty("nb:headline")
    @JsonProperty("Headline")
    private String headline;

    @SemanticProperty("nb:text")
    @JsonProperty("Text")
    private String text;

    @SemanticProperties({
            @SemanticProperty("nb:continue"),
            @SemanticProperty("continue")
    })
    @JsonProperty("Continue")
    private String continue_;

    @SemanticProperty("nb:link")
    @JsonProperty("Link")
    private Link link;
}
