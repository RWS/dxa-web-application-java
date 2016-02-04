package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

/**
 * <p>TagLink class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@SemanticEntity(entityName = "SocialLink", vocabulary = SDL_CORE, prefix = "s")
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

    /**
     * <p>Getter for the field <code>url</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUrl() {
        return url;
    }

    /**
     * <p>Setter for the field <code>url</code>.</p>
     *
     * @param url a {@link java.lang.String} object.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * <p>Getter for the field <code>tag</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.entity.Tag} object.
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * <p>Setter for the field <code>tag</code>.</p>
     *
     * @param tag a {@link com.sdl.webapp.common.api.model.entity.Tag} object.
     */
    public void setTag(Tag tag) {
        this.tag = tag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TagLink{" +
                "url='" + url + '\'' +
                ", tag=" + tag +
                '}';
    }
}
