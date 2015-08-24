package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.util.List;

public class SitemapItem extends AbstractEntity {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Url")
    private String url;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Items")
    private List<SitemapItem> items;

    @JsonProperty("PublishedDate")
    private DateTime publishedDate;

    @JsonProperty("Visible")
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

    public DateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(DateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
