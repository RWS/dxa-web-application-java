package com.sdl.tridion.referenceimpl.common.model;

import com.sdl.tridion.referenceimpl.common.model.Entity;

public class EntityImpl implements Entity {

    private final String id;
    private final String viewName;

    public EntityImpl(String id, String viewName) {
        this.id = id;
        this.viewName = viewName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getViewName() {
        return viewName;
    }

    @Override
    public String toString() {
        return String.format("EntityImpl { id=%s, viewName=%s }", id, viewName);
    }
}
