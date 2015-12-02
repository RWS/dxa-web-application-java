package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.MvcDataImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.UUID;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.button;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.div;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.i;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.iframe;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.img;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.span;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@SemanticEntity(entityName = "VideoObject", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class YouTubeVideo extends MediaItem {

    private static final Logger LOG = LoggerFactory.getLogger(YouTubeVideo.class);


    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 390;

    final MediaHelper mediaHelper = ApplicationContextHolder.getContext().getBean(MediaHelper.class);

    @JsonProperty("Headline")
    private String headline;

    @JsonProperty("YouTubeId")
    private String youTubeId;

    @JsonProperty("Width")
    private int width = DEFAULT_WIDTH;

    @JsonProperty("Height")
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
    public void readFromXhtmlElement(Node xhtmlElement) {
        super.readFromXhtmlElement(xhtmlElement);
        this.setYouTubeId(xhtmlElement.getAttributes().getNamedItem("data-youTubeId").getNodeValue());
        this.setHeadline(xhtmlElement.getAttributes().getNamedItem("data-headline").getNodeValue());

        this.setMvcData(getMvcData());
    }

    @Override
    public MvcData getMvcData() {
        return new MvcDataImpl("Core:Entity:YouTubeVideo").defaults(MvcDataImpl.Defaults.CORE_ENTITY);
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor) throws DxaException {
        return toHtmlElement(widthFactor, 0, "", 0);
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize) {
        return toHtmlElement(widthFactor, aspect, cssClass, containerSize, "");
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {
        return isEmpty(getUrl()) ?
                getYouTubeEmbed(cssClass) :
                getYouTubePlaceholder(widthFactor, aspect, cssClass, containerSize, contextPath);
    }

    private HtmlElement getYouTubePlaceholder(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {

        double imageAspect = (aspect == 0.0) ? mediaHelper.getDefaultMediaAspect() : aspect;

        String placeholderImageUrl = mediaHelper.getResponsiveImageUrl(this.getUrl(), widthFactor, imageAspect, containerSize);

        final HtmlElement playButtonOverlay = i().withClass("fa fa-play-circle").build();

        return (getIsEmbedded() ? span() : div())
                .withAttribute("class", "embed-video")
                .withNode(
                        img(contextPath + placeholderImageUrl).withAlt(getHeadline()).build())
                .withNode(
                        button("button")
                                .withAttribute("data-video", getYouTubeId())
                                .withClass(cssClass)
                                .withNode(playButtonOverlay)
                                .build())
                .build();
    }

    private HtmlElement getYouTubeEmbed(String cssClass) {
        return iframe()
                .withId("video" + UUID.randomUUID().toString())
                .withAttribute("src", "https://www.youtube.com/embed/" + getYouTubeId() + "?version=3&enablejsapi=1")
                .withAttribute("allowfullscreen", "true")
                .withAttribute("frameborder", "0")
                .withClass(cssClass)
                .build();
    }


}
