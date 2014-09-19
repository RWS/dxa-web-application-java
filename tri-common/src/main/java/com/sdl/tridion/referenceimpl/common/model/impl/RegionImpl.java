package com.sdl.tridion.referenceimpl.common.model.impl;

import com.google.common.collect.ImmutableList;
import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.Region;

import java.util.List;

public class RegionImpl implements Region {

    private final String viewName;
    private final List<Entity> entities;

    public RegionImpl(String viewName, List<? extends Entity> entities) {
        this.viewName = viewName;
        this.entities = ImmutableList.copyOf(entities);
    }

    @Override
    public String getViewName() {
        return viewName;
    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public String toString() {
        return String.format("RegionImpl { viewName=%s, entities=%s }", viewName, entities);
    }
}
