package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

@SemanticEntity(entityName = "GenericWidget", vocabulary = SDL_CORE, prefix = "gw", public_ = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class GenericWidget extends AbstractEntityModel {

    @SemanticProperty("gw:parameters")
    private Map<String, String> parameters;

    // Link to same widget with "full" CT
    //
    @SemanticProperty("gw:_self")
    private String link;

    // TODO: Introduce also named links so the widget can find links to other widgets
}
