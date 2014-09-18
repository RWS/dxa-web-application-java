package com.sdl.tridion.referenceimpl.common.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * View model object that represents an entity.
 */
public final class Entity {

    public static final class Builder {
        private String id;
        private String viewName;

        private Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setViewName(String viewName) {
            this.viewName = viewName;
            return this;
        }

        public Entity build() {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "id is required");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(viewName), "viewName is required");

            return new Entity(this);
        }
    }

    private final String id;
    private final String viewName;

    private Entity(Builder builder) {
        this.id = builder.id;
        this.viewName = builder.viewName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getViewName() {
        return viewName;
    }
}
