package com.sdl.tridion.referenceimpl.common.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * View model object that represents a page.
 */
public final class Page {

    public static final class Builder {
        private String id;
        private String title;
        private final ImmutableMap.Builder<String, Region> regions = new ImmutableMap.Builder<>();
        private String viewName;

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

        public Builder addRegions(Iterable<Region> regions) {
            for (Region region : regions) {
                this.regions.put(region.getViewName(), region);
            }
            return this;
        }

        public Builder setViewName(String viewName) {
            this.viewName = viewName;
            return this;
        }

        public Page build() {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "id is required");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(title), "title is required");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(viewName), "viewName is required");

            return new Page(this);
        }
    }

    private final String id;
    private final String title;
    private final Map<String, Region> regions;
    private final String viewName;

    private Page(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.regions = builder.regions.build();
        this.viewName = builder.viewName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Map<String, Region> getRegions() {
        return regions;
    }

    public String getViewName() {
        return viewName;
    }
}
