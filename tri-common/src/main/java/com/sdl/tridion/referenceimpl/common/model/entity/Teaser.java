package com.sdl.tridion.referenceimpl.common.model.entity;

import com.sdl.tridion.referenceimpl.common.model.EntityImpl;

public class Teaser extends EntityImpl {

    public static final class Builder extends EntityImpl.Builder<Builder, Teaser> {
        private String headline;

        public Builder setHeadline(String headline) {
            this.headline = headline;
            return this;
        }

        @Override
        public Teaser build() {
            return new Teaser(this);
        }
    }

    private final String headline;

    private Teaser(Builder builder) {
        super(builder);
        this.headline = builder.headline;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getHeadline() {
        return headline;
    }
}
