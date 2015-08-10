package com.sdl.webapp.main.taglib.tri;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.Download;
import com.sdl.webapp.common.api.model.entity.Image;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.api.model.entity.YouTubeVideo;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MediaTag extends HtmlElementTag {
    private static final Logger LOG = LoggerFactory.getLogger(MediaTag.class);

    private static final HtmlAttribute CLASS_EMBED_VIDEO_ATTR = new HtmlAttribute("class", "embed-video");
    private static final HtmlElement PLAY_BUTTON_OVERLAY = HtmlBuilders.i().withClass("fa fa-play-circle").build();
    private static final HtmlAttribute ALLOWFULLSCREEN_ATTR = new HtmlAttribute("allowfullscreen", "true");
    private static final HtmlAttribute FRAMEBORDER_ATTR = new HtmlAttribute("frameborder", "0");

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

    private static final String[] DOWNLOAD_SIZE_UNITS = { "B", "KB", "MB", "GB", "TB", "PB", "EB" };

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

        if (media instanceof Image) {
            return generateImage();
        } else if (media instanceof YouTubeVideo) {
            return generateYouTubeVideo();
        } else if (media instanceof Download) {
            return generateDownload();
        }

        LOG.error("Unsupported type of media object: {}", media.getClass().getName());
        return null;
    }

    private HtmlElement generateImage() {
        final Image image = (Image) media;
        if (Strings.isNullOrEmpty(image.getUrl())) {
            LOG.warn("Skipping image with empty URL: {}", image);
            return null;
        }

        final MediaHelper mediaHelper = getMediaHelper();

        String imgWidth = widthFactor;

        if (Strings.isNullOrEmpty(widthFactor)) {
            widthFactor = mediaHelper.getDefaultMediaFill();
        }

        final String imageUrl = mediaHelper.getResponsiveImageUrl(image.getUrl(), widthFactor, aspect, containerSize);

        return HtmlBuilders.img(getContextPath() + imageUrl)
                .withAlt(image.getAlternateText())
                .withClass(cssClass)
                .withWidth(imgWidth)
                .withAttribute("data-aspect", String.format(Locale.US, "%.2f", aspect))
                .build();
    }

    private HtmlElement generateYouTubeVideo() {
        final YouTubeVideo video = (YouTubeVideo) media;
        if (Strings.isNullOrEmpty(video.getYouTubeId())) {
            LOG.warn("Skipping YouTube video with empty YouTube ID: {}", video);
            return null;
        }

        return !Strings.isNullOrEmpty(video.getUrl()) ? getYouTubePlaceholder(video) : getYouTubeEmbed(video);
    }

    private HtmlElement getYouTubePlaceholder(YouTubeVideo video) {
        final MediaHelper mediaHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(
                pageContext.getServletContext()).getBean(MediaHelper.class);

        final double imageAspect = aspect == 0.0 ? mediaHelper.getDefaultMediaAspect() : aspect;

        final String placeholderImageUrl = mediaHelper.getResponsiveImageUrl(video.getUrl(), widthFactor, imageAspect,
                containerSize);

        return HtmlBuilders.div()
                .withAttribute(CLASS_EMBED_VIDEO_ATTR)
                .withContent(HtmlBuilders.img(getContextPath() + placeholderImageUrl).withAlt(video.getHeadline()).build())
                .withContent(HtmlBuilders.button("button")
                        .withAttribute("data-video", video.getYouTubeId())
                        .withClass(cssClass)
                        .withContent(PLAY_BUTTON_OVERLAY)
                        .build())
                .build();
    }

    private HtmlElement getYouTubeEmbed(YouTubeVideo video) {
        return HtmlBuilders.iframe()
                .withId("video" + UUID.randomUUID().toString())
                .withAttribute("src", "https://www.youtube.com/embed/" + video.getYouTubeId() + "?version=3&enablejsapi=1")
                .withAttribute(ALLOWFULLSCREEN_ATTR)
                .withAttribute(FRAMEBORDER_ATTR)
                .withClass(cssClass)
                .build();
    }

    private HtmlElement generateDownload() {
        final Download download = (Download) media;
        if (Strings.isNullOrEmpty(download.getUrl())) {
            LOG.warn("Skipping download with empty URL: {}", download);
            return null;
        }

        // TODO: this does not contain any XPM markup

        final String fileType = DOWNLOAD_MIME_TYPES.get(download.getMimeType());
        final String iconClass = fileType == null ? "fa-file" : ("fa-file-" + fileType + "-o");

        final SimpleElementBuilder innerDivBuilder = HtmlBuilders.div()
                .withContent(HtmlBuilders.a(download.getUrl())
                        .withContent(download.getFileName())
                        .build())
                .withContent(HtmlBuilders.element("small")
                        .withClass("size")
                        .withContent("(" + getFriendlyFileSize(download.getFileSize()) + ")")
                        .build());

        if (!Strings.isNullOrEmpty(download.getDescription())) {
            innerDivBuilder.withContent(HtmlBuilders.element("small")
                    .withContent(download.getDescription())
                    .build());
        }

        return HtmlBuilders.div()
                .withClass("download-list")
                .withContent(HtmlBuilders.i().withClass("fa " + iconClass).build())
                .withContent(innerDivBuilder.build())
                .build();
    }

    private String getFriendlyFileSize(long sizeInBytes) {
        double len = sizeInBytes;
        int order = 0;
        while (len >= 1024 && order + 1 < DOWNLOAD_SIZE_UNITS.length) {
            order++;
            len = len / 1024;
        }

        return ((long) Math.ceil(len)) + " " + DOWNLOAD_SIZE_UNITS[order];
    }

    private String getContextPath() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getContextPath();
    }

    private MediaHelper getMediaHelper() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(MediaHelper.class);
    }
}
