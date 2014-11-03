package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import java.util.Date;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

@SemanticEntities({
        @SemanticEntity(entityName = "Teaser", vocabulary = SDL_CORE, prefix = "t"),
        @SemanticEntity(entityName = "Image", vocabulary = SDL_CORE, prefix = "i"),
        @SemanticEntity(entityName = "Article", vocabulary = SDL_CORE, prefix = "a"),
        @SemanticEntity(entityName = "Place", vocabulary = SDL_CORE, prefix = "p"),
        @SemanticEntity(entityName = "LinkedContent", vocabulary = SDL_CORE, prefix = "c")
})
public class Teaser extends AbstractEntity {

    @SemanticProperties({
            @SemanticProperty("a:_self"),
            @SemanticProperty("p:_self"),
            @SemanticProperty("c:link")
    })
    private EmbeddedLink link;

    @SemanticProperties({
            @SemanticProperty("headline"),
            @SemanticProperty("subheading"),
            @SemanticProperty("p:name"),
            @SemanticProperty("c:headline"),
            @SemanticProperty("c:subheading")
    })
    private String headline;

    @SemanticProperties({
            @SemanticProperty("i:_self"),
            @SemanticProperty("a:image"),
            @SemanticProperty("c:media")
    })
    private MediaItem media;

    @SemanticProperties({
            @SemanticProperty("content"),
            @SemanticProperty("a:introText"),
            @SemanticProperty("c:text"),
            @SemanticProperty("c:content")
    })
    private String text;

    @SemanticProperty("c:date")
    private Date date;

    @SemanticProperties({
            @SemanticProperty("p:location"),
            @SemanticProperty("c:location")
    })
    private Location location;

    public EmbeddedLink getLink() {
        return link;
    }

    public void setLink(EmbeddedLink link) {
        this.link = link;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public MediaItem getMedia() {
        return media;
    }

    public void setMedia(MediaItem media) {
        this.media = media;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Teaser{" +
                "link=" + link +
                ", headline='" + headline + '\'' +
                ", media=" + media +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", location=" + location +
                '}';
    }
}
