package com.sdl.tridion.referenceimpl.common.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.util.List;

public final class RegionImpl implements Region {

    public static final class Builder {
        private String viewName;
        private final ImmutableList.Builder<Entity> entitiesBuilder = ImmutableList.builder();

        private Builder() {
        }

        public Builder setViewName(String viewName) {
            this.viewName = viewName;
            return this;
        }

        public Builder addEntity(Entity entity) {
            this.entitiesBuilder.add(entity);
            return this;
        }

        public Builder addEntities(Iterable<? extends Entity> entities) {
            this.entitiesBuilder.addAll(entities);
            return this;
        }

        public RegionImpl build() {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(viewName), "viewName is required");

            return new RegionImpl(this);
        }
    }

    private final String viewName;
    private final List<Entity> entities;

    private RegionImpl(Builder builder) {
        this.viewName = builder.viewName;
        this.entities = builder.entitiesBuilder.build();
    }

    public static Builder newBuilder() {
        return new Builder();
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
