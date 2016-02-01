package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.util.List;

/**
 * <p>SitemapItem class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class SitemapItem extends AbstractEntityModel {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Url")
    private String url;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Items")
    private List<SitemapItem> items;

    @JsonProperty("PublishedDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private DateTime publishedDate;

    @JsonProperty("Visible")
    private boolean visible;

    /**
     * <p>Getter for the field <code>title</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTitle() {
        return title;
    }

    /**
     * <p>Setter for the field <code>title</code>.</p>
     *
     * @param title a {@link java.lang.String} object.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>Getter for the field <code>url</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUrl() {
        return url;
    }

    /**
     * <p>Setter for the field <code>url</code>.</p>
     *
     * @param url a {@link java.lang.String} object.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link java.lang.String} object.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>Getter for the field <code>items</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<SitemapItem> getItems() {
        return items;
    }

    /**
     * <p>Setter for the field <code>items</code>.</p>
     *
     * @param items a {@link java.util.List} object.
     */
    public void setItems(List<SitemapItem> items) {
        this.items = items;
    }

    /**
     * <p>Getter for the field <code>publishedDate</code>.</p>
     *
     * @return a {@link org.joda.time.DateTime} object.
     */
    public DateTime getPublishedDate() {
        return publishedDate;
    }

    /**
     * <p>Setter for the field <code>publishedDate</code>.</p>
     *
     * @param publishedDate a {@link org.joda.time.DateTime} object.
     */
    public void setPublishedDate(DateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    /**
     * <p>isVisible.</p>
     *
     * @return a boolean.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * <p>Setter for the field <code>visible</code>.</p>
     *
     * @param visible a boolean.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
