package com.sdl.tridion.referenceimpl.common.model.entity;

import com.sdl.tridion.referenceimpl.common.model.EntityImpl;

public class ContentList extends EntityImpl {

    public static final class Builder extends EntityImpl.Builder<Builder, ContentList> {

        @Override
        public ContentList build() {
            return new ContentList(this);
        }
    }

    private ContentList(Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
