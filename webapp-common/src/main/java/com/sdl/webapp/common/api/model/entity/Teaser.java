package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.*;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import java.util.Date;

@SemanticEntities({
        @SemanticEntity(entityName = "Teaser", vocab = Vocabularies.SDL_CORE, prefix = "t"),
        @SemanticEntity(entityName = "Image", vocab = Vocabularies.SDL_CORE, prefix = "i"),
        @SemanticEntity(entityName = "Article", vocab = Vocabularies.SDL_CORE, prefix = "a"),
        @SemanticEntity(entityName = "Place", vocab = Vocabularies.SDL_CORE, prefix = "p")
})
public class Teaser extends EntityBase {

    @SemanticProperties({
            @SemanticProperty("a:_self"),
            @SemanticProperty("p:_self")
    })
    private Link link;

    @SemanticProperties({
            @SemanticProperty("headline"),
            @SemanticProperty("subheading"),
            @SemanticProperty("p:name")
    })
    private String headline;

    @SemanticProperties({
            @SemanticProperty("i:_self"),
            @SemanticProperty("a:image")
    })
    private MediaItem media;

    @SemanticProperties({
            @SemanticProperty("content"),
            @SemanticProperty("a:introText")
    })
    private String text;

    private Date date;

    private Location location;

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
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
}
