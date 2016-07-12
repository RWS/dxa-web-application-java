package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.RichText;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

@SemanticEntities({
        @SemanticEntity(entityName = "Image", vocabulary = SDL_CORE, prefix = "i"),
        @SemanticEntity(entityName = "Article", vocabulary = SDL_CORE, prefix = "a"),
        @SemanticEntity(entityName = "Place", vocabulary = SDL_CORE, prefix = "p"),
        @SemanticEntity(entityName = "LinkedContent", vocabulary = SDL_CORE, prefix = "c"),
        @SemanticEntity(entityName = "StandardMetadata", vocabulary = SDL_CORE, prefix = "m")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Teaser extends AbstractEntityModel {

    @SemanticProperties({
            @SemanticProperty("a:_self"),
            @SemanticProperty("p:_self"),
            @SemanticProperty("c:link")
    })
    @JsonProperty("Link")
    private Link link;

    @SemanticProperties({
            @SemanticProperty("headline"),
            @SemanticProperty("subheading"),
            @SemanticProperty("a:headline"),
            @SemanticProperty("p:name"),
            @SemanticProperty("c:headline"),
            @SemanticProperty("c:subheading")
    })
    @JsonProperty("Headline")
    private String headline;

    @SemanticProperties({
            @SemanticProperty("i:_self"),
            @SemanticProperty("a:image"),
            @SemanticProperty("c:media")
    })
    @JsonProperty("Media")
    private MediaItem media;

    @SemanticProperties({
            @SemanticProperty("content"),
            @SemanticProperty("a:introText"),
            @SemanticProperty("c:text"),
            @SemanticProperty("c:content"),
            @SemanticProperty("m:introText"),
            @SemanticProperty("m:description"),
    })
    @JsonProperty("Text")
    private RichText text;

    @SemanticProperties({
            @SemanticProperty("c:date"),
            @SemanticProperty("m:dateCreated")
    })
    @JsonProperty("Date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private DateTime date;

    @SemanticProperties({
            @SemanticProperty("p:location"),
            @SemanticProperty("c:location")
    })
    @JsonProperty("Location")
    private Location location;

    public RichText getText() {
        return text != null ? text : new RichText("");
    }
}
