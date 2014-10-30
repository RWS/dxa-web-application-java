package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

@SemanticEntity(entityName = "LinkList", vocabulary = SDL_CORE)
public class EmbeddedLinkList extends AbstractEntity {

    private String headline;

    private List<EmbeddedLink> links;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public List<EmbeddedLink> getLinks() {
        return links;
    }

    public void setLinks(List<EmbeddedLink> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "LinkList{" +
                "headline='" + headline + '\'' +
                ", links=" + links +
                '}';
    }
}
