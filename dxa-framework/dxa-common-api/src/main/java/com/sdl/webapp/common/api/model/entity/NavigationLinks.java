package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * <p>NavigationLinks class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class NavigationLinks extends AbstractEntityModel {

    @JsonProperty("Items")
    private List<Link> items;

    /**
     * <p>Constructor for NavigationLinks.</p>
     */
    public NavigationLinks() {
    }

    /**
     * <p>Constructor for NavigationLinks.</p>
     *
     * @param items a {@link java.util.List} object.
     */
    public NavigationLinks(List<Link> items) {
        this.items = items;
    }

    /**
     * <p>Getter for the field <code>items</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Link> getItems() {
        return items;
    }

    /**
     * <p>Setter for the field <code>items</code>.</p>
     *
     * @param items a {@link java.util.List} object.
     */
    public void setItems(List<Link> items) {
        this.items = items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "NavigationLinks{" +
                "items=" + items +
                '}';
    }
}
