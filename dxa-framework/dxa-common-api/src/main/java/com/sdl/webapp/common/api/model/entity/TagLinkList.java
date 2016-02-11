package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

/**
 * <p>TagLinkList class.</p>
 */
@SemanticEntities({
        @SemanticEntity(entityName = "LinkList", vocabulary = SDL_CORE),
        @SemanticEntity(entityName = "SocialLinks", vocabulary = SDL_CORE, prefix = "s")
})
public class TagLinkList extends AbstractEntityModel {

    @JsonProperty("Headline")
    private String headline;

    @SemanticProperty("s:link")
    @JsonProperty("Links")
    private List<TagLink> links;

    /**
     * <p>Getter for the field <code>headline</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * <p>Setter for the field <code>headline</code>.</p>
     *
     * @param headline a {@link java.lang.String} object.
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     * <p>Getter for the field <code>links</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<TagLink> getLinks() {
        return links;
    }

    /**
     * <p>Setter for the field <code>links</code>.</p>
     *
     * @param links a {@link java.util.List} object.
     */
    public void setLinks(List<TagLink> links) {
        this.links = links;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TagLinkList{" +
                "headline='" + headline + '\'' +
                ", links=" + links +
                '}';
    }
}
