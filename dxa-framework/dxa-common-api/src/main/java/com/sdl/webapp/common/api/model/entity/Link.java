package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

/**
 * <p>Link class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@SemanticEntity(entityName = "EmbeddedLink", vocabulary = SDL_CORE, prefix = "e")
public class Link extends AbstractEntityModel {

    @SemanticProperties({
            @SemanticProperty("internalLink"),
            @SemanticProperty("externalLink"),
            @SemanticProperty("e:internalLink"),
            @SemanticProperty("e:externalLink")
    })
    @JsonProperty("Url")
    private String url;

    @JsonProperty("LinkText")
    @SemanticProperty("e:linkText")
    private String linkText;

    @SemanticProperty("e:alternateText")
    @JsonProperty("AlternateText")
    private String alternateText;

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
     * <p>Getter for the field <code>linkText</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLinkText() {
        return linkText;
    }

    /**
     * <p>Setter for the field <code>linkText</code>.</p>
     *
     * @param linkText a {@link java.lang.String} object.
     */
    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    /**
     * <p>Getter for the field <code>alternateText</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAlternateText() {
        return alternateText;
    }

    /**
     * <p>Setter for the field <code>alternateText</code>.</p>
     *
     * @param alternateText a {@link java.lang.String} object.
     */
    public void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Link{" +
                "url='" + url + '\'' +
                ", linkText='" + linkText + '\'' +
                ", alternateText='" + alternateText + '\'' +
                '}';
    }
}
