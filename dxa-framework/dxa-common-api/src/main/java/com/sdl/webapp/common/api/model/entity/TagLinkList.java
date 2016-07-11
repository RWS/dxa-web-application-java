package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

@SemanticEntities({
        @SemanticEntity(entityName = "LinkList", vocabulary = SDL_CORE),
        @SemanticEntity(entityName = "SocialLinks", vocabulary = SDL_CORE, prefix = "s")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class TagLinkList extends AbstractEntityModel {

    @JsonProperty("Headline")
    private String headline;

    @SemanticProperty("s:link")
    @JsonProperty("Links")
    private List<TagLink> links;
}
