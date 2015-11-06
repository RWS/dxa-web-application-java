package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.RichText;
import org.joda.time.DateTime;

import java.util.Objects;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

@SemanticEntities({
        @SemanticEntity(entityName = "Image", vocabulary = SDL_CORE, prefix = "i"),
        @SemanticEntity(entityName = "Article", vocabulary = SDL_CORE, prefix = "a"),
        @SemanticEntity(entityName = "Place", vocabulary = SDL_CORE, prefix = "p"),
        @SemanticEntity(entityName = "LinkedContent", vocabulary = SDL_CORE, prefix = "c"),
        @SemanticEntity(entityName = "StandardMetadata", vocabulary = SDL_CORE, prefix = "m")
})
public class Teaser extends AbstractEntityModel {

    @SemanticProperties({
            @SemanticProperty("a:_self"),
            @SemanticProperty("p:_self"),
            @SemanticProperty("c:link")
    })
    @JsonProperty("Link")
    private Link link;

    @SemanticProperties({
            @SemanticProperty("headline"),
            @SemanticProperty("subheading"),
            @SemanticProperty("a:headline"),
            @SemanticProperty("p:name"),
            @SemanticProperty("c:headline"),
            @SemanticProperty("c:subheading")
    })
    @JsonProperty("Headline")
    private String headline;

    @SemanticProperties({
            @SemanticProperty("i:_self"),
            @SemanticProperty("a:image"),
            @SemanticProperty("c:media")
    })
    @JsonProperty("Media")
    private MediaItem media;

    @SemanticProperties({
            @SemanticProperty("content"),
            @SemanticProperty("a:introText"),
            @SemanticProperty("c:text"),
            @SemanticProperty("c:content"),
            @SemanticProperty("m:introText"),
            @SemanticProperty("m:description"),
    })
    @JsonProperty("Text")
    private RichText text;

    @SemanticProperties({
            @SemanticProperty("c:date"),
            @SemanticProperty("m:dateCreated")
    })
    @JsonProperty("Date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private DateTime date;

    @SemanticProperties({
            @SemanticProperty("p:location"),
            @SemanticProperty("c:location")
    })
    @JsonProperty("Location")
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

    public RichText getText() {
        return text != null ? text : new RichText("");
    }

    public void setText(RichText text) {
        this.text = text;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Teaser teaser = (Teaser) o;
        return Objects.equals(link, teaser.link) &&
                Objects.equals(headline, teaser.headline) &&
                Objects.equals(media, teaser.media) &&
                Objects.equals(text, teaser.text) &&
                Objects.equals(date, teaser.date) &&
                Objects.equals(location, teaser.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), link, headline, media, text, date, location);
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
