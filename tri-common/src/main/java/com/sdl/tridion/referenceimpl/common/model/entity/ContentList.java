package com.sdl.tridion.referenceimpl.common.model.entity;

import com.google.common.collect.ImmutableList;
import com.sdl.tridion.referenceimpl.common.model.EntityImpl;

import java.util.List;

public class ContentList<T> extends EntityImpl {

    public static final class Builder<T> extends EntityImpl.Builder<Builder<T>, ContentList<T>> {
        private String headline;
        private final ImmutableList.Builder<T> itemListElementsBuilder = ImmutableList.builder();

        public Builder<T> setHeadline(String headline) {
            this.headline = headline;
            return this;
        }

        public Builder<T> addItemListElement(T itemListElement) {
            this.itemListElementsBuilder.add(itemListElement);
            return this;
        }

        @Override
        public ContentList<T> build() {
            return new ContentList<>(this);
        }
    }

    private final String headline;
    private final List<T> itemListElements;

    private ContentList(Builder<T> builder) {
        super(builder);
        this.headline = builder.headline;
        this.itemListElements = builder.itemListElementsBuilder.build();
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    public String getHeadline() {
        return headline;
    }

    public List<T> getItemListElements() {
        return itemListElements;
    }
}
