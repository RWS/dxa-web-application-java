package com.sdl.tridion.referenceimpl.common.model.entity;

import com.sdl.tridion.referenceimpl.common.model.EntityImpl;

public class Teaser extends EntityImpl {

    public static final class Builder extends EntityImpl.Builder<Builder, Teaser> {

        @Override
        public Teaser build() {
            return new Teaser(this);
        }
    }

    private Teaser(Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
