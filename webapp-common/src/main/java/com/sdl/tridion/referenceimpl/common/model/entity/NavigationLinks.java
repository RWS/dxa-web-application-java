package com.sdl.tridion.referenceimpl.common.model.entity;

import java.util.List;

public class NavigationLinks extends EntityBase {

    private List<Link> items;

    public List<Link> getItems() {
        return items;
    }

    public void setItems(List<Link> items) {
        this.items = items;
    }
}
