package com.sdl.tridion.referenceimpl.common.model.entity;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sdl.tridion.referenceimpl.common.model.EntityImpl;

public final class YouTubeVideo extends EntityImpl {

    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 390;

    public static final class Builder extends EntityImpl.Builder<Builder, YouTubeVideo> {
        private String headline;
        private String youTubeId;
        private int width = DEFAULT_WIDTH;
        private int height = DEFAULT_HEIGHT;

        private Builder() {
        }

        public Builder setHeadline(String headline) {
            this.headline = headline;
            return this;
        }

        public Builder setYouTubeId(String youTubeId) {
            this.youTubeId = youTubeId;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        @Override
        public YouTubeVideo build() {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(youTubeId), "youTubeId is required");

            return new YouTubeVideo(this);
        }
    }

    private final String headline;
    private final String youTubeId;
    private final int width;
    private final int height;

    private YouTubeVideo(Builder builder) {
        super(builder);

        this.headline = builder.headline;
        this.youTubeId = builder.youTubeId;
        this.width = builder.width;
        this.height = builder.height;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getHeadline() {
        return headline;
    }

    public String getYouTubeId() {
        return youTubeId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
