package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

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

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContinue_() {
        return continue_;
    }

    public void setContinue_(String continue_) {
        this.continue_ = continue_;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

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
