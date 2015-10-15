package com.sdl.webapp.common.impl.taglib.dxa;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.markup.html.HtmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Map;

public class MediaTag extends HtmlElementTag {
    private static final Logger LOG = LoggerFactory.getLogger(MediaTag.class);


    // TODO: configurize the mime type to Font Awesome mapping
    // filetypes supported by http://fortawesome.github.io/Font-Awesome/icons/#file-type
    private static final Map<String, String> DOWNLOAD_MIME_TYPES = ImmutableMap.<String, String>builder()
            .put("application/ms-excel", "excel")
            .put("application/pdf", "pdf")
            .put("application/x-wav", "audio")
            .put("audio/x-mpeg", "audio")
            .put("application/msword", "word")
            .put("text/rtf", "word")
            .put("application/zip", "archive")
            .put("image/gif", "image")
            .put("image/jpeg", "image")
            .put("image/png", "image")
            .put("image/x-bmp", "image")
            .put("text/plain", "text")
            .put("text/css", "code")
            .put("application/x-javascript", "code")
            .put("application/ms-powerpoint", "powerpoint")
            .put("video/vnd.rn-realmedia", "video")
            .put("video/quicktime", "video")
            .put("video/mpeg", "video")
            .build();

    private static final String[] DOWNLOAD_SIZE_UNITS = {"B", "KB", "MB", "GB", "TB", "PB", "EB"};

    private MediaItem media;
    private String widthFactor;
    private double aspect;
    private String cssClass;
    private int containerSize;

    public void setMedia(MediaItem media) {
        this.media = media;
    }

    public void setWidthFactor(String widthFactor) {
        this.widthFactor = widthFactor;
    }

    public void setAspect(double aspect) {
        this.aspect = aspect;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setContainerSize(int containerSize) {
        this.containerSize = containerSize;
    }

    @Override
    protected HtmlElement generateElement() {
        if (media == null) {
            return null;
        }
/*
        if (media instanceof Image) {
            return generateImage();
        } else if (media instanceof YouTubeVideo) {
            return generateYouTubeVideo();
        } else if (media instanceof Download) {
            return generateDownload();
        }
*/

        return media.toHtmlElement(this.widthFactor, this.aspect, this.cssClass, this.containerSize, this.getContextPath());

    }


    private String getContextPath() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getContextPath();
    }


}
