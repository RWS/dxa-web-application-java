package com.sdl.webapp.common.api.model.entity;

import java.util.List;

public class NavigationLinks extends AbstractEntity {

    private List<Link> items;

    public NavigationLinks() {
    }

    public NavigationLinks(List<Link> items) {
        this.items = items;
    }

    public List<Link> getItems() {
        return items;
    }

    public void setItems(List<Link> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "NavigationLinks{" +
                "items=" + items +
                '}';
    }
}
