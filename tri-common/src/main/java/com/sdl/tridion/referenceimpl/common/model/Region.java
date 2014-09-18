package com.sdl.tridion.referenceimpl.common.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * View model object that represents a region.
 */
public final class Region {

    public static final class Builder {
        private String viewName;
        private final ImmutableList.Builder<Entity> entities = new ImmutableList.Builder<>();

        private Builder() {
        }

        public Builder setViewName(String viewName) {
            this.viewName = viewName;
            return this;
        }

        public Builder addEntity(Entity entity) {
            this.entities.add(entity);
            return this;
        }

        public Region build() {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(viewName), "viewName is required");

            return new Region(this);
        }
    }

    private final String viewName;
    private final List<Entity> entities;

    private Region(Builder builder) {
        this.viewName = builder.viewName;
        this.entities = builder.entities.build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getViewName() {
        return viewName;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public String toString() {
        return String.format("Region { viewName=%s, entities=%s }", viewName, entities);
    }
}
