package com.sdl.tridion.referenceimpl.common.model.entity;

import java.util.List;

public class LinkList<T> extends EntityBase {

    private String headline;
    private List<T> links;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public List<T> getLinks() {
        return links;
    }

    public void setLinks(List<T> links) {
        this.links = links;
    }
}
