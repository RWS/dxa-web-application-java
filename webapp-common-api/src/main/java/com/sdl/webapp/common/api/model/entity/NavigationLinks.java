package com.sdl.webapp.common.api.model.entity;

import java.util.List;

public class NavigationLinks extends AbstractEntity {

    private List<EmbeddedLink> items;

    public List<EmbeddedLink> getItems() {
        return items;
    }

    public void setItems(List<EmbeddedLink> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "NavigationLinks{" +
                "items=" + items +
                '}';
    }
}
