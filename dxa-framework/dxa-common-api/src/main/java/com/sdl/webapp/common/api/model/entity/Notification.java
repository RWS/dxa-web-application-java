package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

/**
 * <p>Notification class.</p>
 */
@SemanticEntity(entityName = "NotificationBar", vocabulary = SDL_CORE, prefix = "nb")
public class Notification extends AbstractEntityModel {

    @SemanticProperty("nb:headline")
    @JsonProperty("Headline")
    private String headline;

    @SemanticProperty("nb:text")
    @JsonProperty("Text")
    private String text;

    @SemanticProperties({
            @SemanticProperty("nb:continue"),
            @SemanticProperty("continue")
    })
    @JsonProperty("Continue")
    private String continue_;

    @SemanticProperty("nb:link")
    @JsonProperty("Link")
    private Link link;

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
     * <p>Getter for the field <code>text</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getText() {
        return text;
    }

    /**
     * <p>Setter for the field <code>text</code>.</p>
     *
     * @param text a {@link java.lang.String} object.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * <p>Getter for the field <code>continue_</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getContinue_() {
        return continue_;
    }

    /**
     * <p>Setter for the field <code>continue_</code>.</p>
     *
     * @param continue_ a {@link java.lang.String} object.
     */
    public void setContinue_(String continue_) {
        this.continue_ = continue_;
    }

    /**
     * <p>Getter for the field <code>link</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.entity.Link} object.
     */
    public Link getLink() {
        return link;
    }

    /**
     * <p>Setter for the field <code>link</code>.</p>
     *
     * @param link a {@link com.sdl.webapp.common.api.model.entity.Link} object.
     */
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Notification{" +
                "headline='" + headline + '\'' +
                ", text='" + text + '\'' +
                ", continue_='" + continue_ + '\'' +
                ", link=" + link +
                '}';
    }
}
