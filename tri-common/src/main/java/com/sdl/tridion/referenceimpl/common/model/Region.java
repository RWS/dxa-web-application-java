package com.sdl.tridion.referenceimpl.common.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * View model object that represents a region.
 */
public final class Region {

    public static final class Builder {
        private String viewName;

        private Builder() {
        }

        public Builder setViewName(String viewName) {
            this.viewName = viewName;
            return this;
        }

        public Region build() {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(viewName), "viewName is required");

            return new Region(this);
        }
    }

    private final String viewName;

    private Region(Builder builder) {
        this.viewName = builder.viewName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getViewName() {
        return viewName;
    }
}
