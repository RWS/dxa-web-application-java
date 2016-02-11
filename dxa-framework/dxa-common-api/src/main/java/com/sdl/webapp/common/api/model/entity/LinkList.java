package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * <p>LinkList class.</p>
 */
public class LinkList extends AbstractEntityModel {

    @JsonProperty("Headline")
    private String headline;

    @JsonProperty("Links")
    private List<Link> links;

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
    public List<Link> getLinks() {
        return links;
    }

    /**
     * <p>Setter for the field <code>links</code>.</p>
     *
     * @param links a {@link java.util.List} object.
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "LinkList{" +
                "headline='" + headline + '\'' +
                ", links=" + links +
                '}';
    }
}
