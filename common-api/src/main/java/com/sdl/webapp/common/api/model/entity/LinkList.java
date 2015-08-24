package com.sdl.webapp.common.api.model.entity;

import java.util.List;

public class LinkList extends AbstractEntity {

    private String headline;

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
