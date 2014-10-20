package com.sdl.webapp.common.model.entity;

import com.sdl.webapp.common.mapping.SemanticEntities;
import com.sdl.webapp.common.mapping.SemanticEntity;
import com.sdl.webapp.common.mapping.SemanticProperties;
import com.sdl.webapp.common.mapping.SemanticProperty;
import com.sdl.webapp.common.model.Entity;

import java.util.Date;

@SemanticEntities({
        @SemanticEntity(entityName = "Teaser", vocab = Entity.CORE_VOCABULARY, prefix = "t"),
        @SemanticEntity(entityName = "Image", vocab = Entity.CORE_VOCABULARY, prefix = "i"),
        @SemanticEntity(entityName = "Article", vocab = Entity.CORE_VOCABULARY, prefix = "a"),
        @SemanticEntity(entityName = "Place", vocab = Entity.CORE_VOCABULARY, prefix = "p")
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
