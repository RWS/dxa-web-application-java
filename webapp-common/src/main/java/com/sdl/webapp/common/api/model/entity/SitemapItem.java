package com.sdl.webapp.common.api.model.entity;

import java.util.List;

public class SitemapItem extends EntityBase {

    private String title;

    private String url;

    private String type;

    private List<SitemapItem> items;

    private String publishedDate;

    private boolean visible;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SitemapItem> getItems() {
        return items;
    }

    public void setItems(List<SitemapItem> items) {
        this.items = items;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
