package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

@SemanticEntities({
        @SemanticEntity(entityName = "LinkList", vocabulary = SDL_CORE),
        @SemanticEntity(entityName = "SocialLinks", vocabulary = SDL_CORE, prefix = "s")
})
public class TagLinkList extends AbstractEntity {

    private String headline;

    @SemanticProperty("s:link")
    private List<TagLink> links;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public List<TagLink> getLinks() {
        return links;
    }

    public void setLinks(List<TagLink> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "TagLinkList{" +
                "headline='" + headline + '\'' +
                ", links=" + links +
                '}';
    }
}
