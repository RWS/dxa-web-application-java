package com.sdl.webapp.common.api.model.entity;

public class YouTubeVideo extends MediaItem {

    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 390;

    private String headline;

    private String youTubeId;

    private int width = DEFAULT_WIDTH;

    private int height = DEFAULT_HEIGHT;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getYouTubeId() {
        return youTubeId;
    }

    public void setYouTubeId(String youTubeId) {
        this.youTubeId = youTubeId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "YouTubeVideo{" +
                "url='" + getUrl() + '\'' +
                ", fileName='" + getFileName() + '\'' +
                ", fileSize=" + getFileSize() +
                ", mimeType='" + getMimeType() + '\'' +
                ", headline='" + headline + '\'' +
                ", youTubeId='" + youTubeId + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
