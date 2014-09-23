package com.sdl.tridion.referenceimpl.common.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PageImpl implements Page {

    public static final class Builder {
        private String id;
        private String title;
        private String viewName;
        private final ImmutableMap.Builder<String, Region> regionsBuilder = ImmutableMap.builder();

        private Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setViewName(String viewName) {
            this.viewName = viewName;
            return this;
        }

        public Builder addRegions(Iterable<? extends Region> regions) {
            for (Region region : regions) {
                this.regionsBuilder.put(region.getViewName(), region);
            }

            return this;
        }

        public PageImpl build() {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "id is required");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(viewName), "viewName is required");

            return new PageImpl(this);
        }
    }

    private final String id;
    private final String title;
    private final String viewName;
    private final Map<String, Region> regions;

    private Map<String, Entity> entities;

    private PageImpl(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.viewName = builder.viewName;
        this.regions = builder.regionsBuilder.build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getViewName() {
        return viewName;
    }

    @Override
    public Map<String, Region> getRegions() {
        return regions;
    }

    @Override
    public Region getRegion(String regionViewName) {
        return getRegions().get(regionViewName);
    }

    @Override
    public synchronized Map<String, Entity> getEntities() {
        if (entities == null) {
            // Note that Guava's ImmutableMap.Builder.put() throws an exception if you try to put in duplicate keys;
            // we avoid that by first putting the entities in a standard LinkedHashMap.
            Map<String, Entity> map = new LinkedHashMap<>();
            for (Region region : getRegions().values()) {
                for (Entity entity : region.getEntities()) {
                    map.put(entity.getId(), entity);
                }
            }

            entities = ImmutableMap.copyOf(map);
        }

        return entities;
    }

    @Override
    public Entity getEntity(String entityId) {
        return getEntities().get(entityId);
    }

    @Override
    public String toString() {
        return String.format("PageImpl { id=%s, title=%s, viewName=%s, regions=%s }",
                id, title, viewName, regions.values());
    }
}
