package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

@SemanticEntities({
        @SemanticEntity(entityName = "GeoCoordinates", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true),
        @SemanticEntity(entityName = "LocationMeta", vocabulary = SDL_CORE, prefix = "lm")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Location extends AbstractEntityModel {

    @SemanticProperties({
            @SemanticProperty("s:latitude"),
            @SemanticProperty("lm:latitude")
    })
    @JsonProperty("Latitude")
    private double latitude;

    @SemanticProperties({
            @SemanticProperty("s:longitude"),
            @SemanticProperty("lm:longitude")
    })
    @JsonProperty("Longitude")
    private double longitude;

    @SemanticProperty("lm:query")
    @JsonProperty("Query")
    private String query;
}
