package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LinkList extends AbstractEntityModel {

    @JsonProperty("Headline")
    private String headline;

    @JsonProperty("Links")
    private List<Link> links;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "LinkList{" +
                "headline='" + headline + '\'' +
                ", links=" + links +
                '}';
    }
}
