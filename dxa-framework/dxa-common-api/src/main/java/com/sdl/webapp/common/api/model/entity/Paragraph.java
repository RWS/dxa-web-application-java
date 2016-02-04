package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.model.RichText;

/**
 * <p>Paragraph class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class Paragraph extends AbstractEntityModel {

    @JsonProperty("Subheading")
    private String subheading;

    @JsonProperty("Content")
    private RichText content;

    @JsonProperty("Media")
    private MediaItem media;

    @JsonProperty("Caption")
    private String caption;

    /**
     * <p>Getter for the field <code>subheading</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSubheading() {
        return subheading;
    }

    /**
     * <p>Setter for the field <code>subheading</code>.</p>
     *
     * @param subheading a {@link java.lang.String} object.
     */
    public void setSubheading(String subheading) {
        this.subheading = subheading;
    }

    /**
     * <p>Getter for the field <code>content</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.RichText} object.
     */
    public RichText getContent() {
        return content;
    }

    /**
     * <p>Setter for the field <code>content</code>.</p>
     *
     * @param content a {@link com.sdl.webapp.common.api.model.RichText} object.
     */
    public void setContent(RichText content) {
        this.content = content;
    }

    /**
     * <p>Getter for the field <code>media</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.entity.MediaItem} object.
     */
    public MediaItem getMedia() {
        return media;
    }

    /**
     * <p>Setter for the field <code>media</code>.</p>
     *
     * @param media a {@link com.sdl.webapp.common.api.model.entity.MediaItem} object.
     */
    public void setMedia(MediaItem media) {
        this.media = media;
    }

    /**
     * <p>Getter for the field <code>caption</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * <p>Setter for the field <code>caption</code>.</p>
     *
     * @param caption a {@link java.lang.String} object.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
}
