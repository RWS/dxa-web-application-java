package com.sdl.dxa.modules.core.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

@SemanticEntities({
        @SemanticEntity(entityName = "ItemList", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true),
        @SemanticEntity(entityName = "ItemList", vocabulary = SDL_CORE, prefix = "i"),
        @SemanticEntity(entityName = "ContentQuery", vocabulary = SDL_CORE, prefix = "q")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class ContentList extends AbstractEntityModel {

    @SemanticProperties({
            @SemanticProperty("s:headline"),
            @SemanticProperty("i:headline"),
            @SemanticProperty("q:headline")
    })
    @JsonProperty("Headline")
    private String headline;

    @SemanticProperties({
            @SemanticProperty("i:link"),
            @SemanticProperty("q:link")
    })
    @JsonProperty("Link")
    private Link link;

    @SemanticProperties({
            @SemanticProperty("i:pageSize"),
            @SemanticProperty("q:pageSize")
    })
    @JsonProperty("PageSize")
    private int pageSize;

    @SemanticProperties({
            @SemanticProperty("i:contentType"),
            @SemanticProperty("q:contentType")
    })
    @JsonProperty("ContentType")
    private Tag contentType;

    @SemanticProperties({
            @SemanticProperty("i:sort"),
            @SemanticProperty("q:sort")
    })
    @JsonProperty("Sort")
    private Tag sort;

    @JsonProperty("Start")
    private int start;

    @JsonProperty("CurrentPage")
    private int currentPage = 1;

    @JsonProperty("HasMore")
    private boolean hasMore;

    @SemanticProperties({
            @SemanticProperty("s:itemListElement"),
            @SemanticProperty("i:itemListElement")
    })
    @JsonProperty("ItemListElements")
    private List<Teaser> itemListElements = new ArrayList<>();
}
