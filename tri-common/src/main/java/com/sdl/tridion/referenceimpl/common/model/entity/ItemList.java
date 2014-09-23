package com.sdl.tridion.referenceimpl.common.model.entity;

import com.google.common.collect.ImmutableList;
import com.sdl.tridion.referenceimpl.common.model.EntityImpl;

import java.util.List;

public class ItemList extends EntityImpl {

    public static final class Builder extends EntityImpl.Builder<Builder, ItemList> {
        private String headline;
        private final ImmutableList.Builder<Teaser> itemListElementsBuilder = ImmutableList.builder();

        public Builder setHeadline(String headline) {
            this.headline = headline;
            return this;
        }

        public Builder addItemListElement(Teaser itemListElement) {
            this.itemListElementsBuilder.add(itemListElement);
            return this;
        }

        @Override
        public ItemList build() {
            return new ItemList(this);
        }
    }

    private final String headline;
    private final List<Teaser> itemListElements;

    private ItemList(Builder builder) {
        super(builder);
        this.headline = builder.headline;
        this.itemListElements = builder.itemListElementsBuilder.build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getHeadline() {
        return headline;
    }

    public List<Teaser> getItemListElements() {
        return itemListElements;
    }
}
