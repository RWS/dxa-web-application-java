package com.sdl.tridion.referenceimpl.common.model.entity;

import com.sdl.tridion.referenceimpl.common.model.EntityImpl;

public class ItemList extends EntityImpl {

    public static final class Builder extends EntityImpl.Builder<Builder, ItemList> {

        @Override
        public ItemList build() {
            return new ItemList(this);
        }
    }

    private ItemList(Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
