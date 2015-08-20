package com.sdl.webapp.common.api.model.entity;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;

public class YouTubeVideo extends MediaItem {

    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 390;

    @Autowired
    private MediaHelper mediaHelper;
    
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

	@Override
	public String toHtml(String widthFactor) {
		return toHtml(widthFactor, 0, "", 0);
	}

	@Override
	public String toHtml(String widthFactor, double aspect, String cssClass, int containerSize) {
		if (Strings.isNullOrEmpty(getUrl()))
        {
            return String.format(
                    "<iframe src=\"https://www.youtube.com/embed/{0}?version=3&enablejsapi=1\" id=\"video{1}\" class=\"{2}\"/>",
                    this.getYouTubeId(), UUID.randomUUID().toString().replaceAll("-", ""), null
                    );
        }

        String htmlTagName = this.getIsEmbedded() ? "span" : "div";
        String placeholderImageUrl = Strings.isNullOrEmpty(getUrl()) ? null : this.mediaHelper.getResponsiveImageUrl(getUrl(), widthFactor, aspect, containerSize);

        return String.format(
                "<{4} class=\"embed-video\"><img src=\"{1}\" alt=\"{2}\"><button type=\"button\" data-video=\"{0}\" class=\"{3}\"><i class=\"fa fa-play-circle\"></i></button></{4}>",
                getYouTubeId(), placeholderImageUrl, getHeadline(), (String) null, htmlTagName
                );
	}
	
	
}
