package com.sdl.dxa.modules.test.model;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.MvcDataImpl;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.UUID;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

@SemanticEntity(entityName = "VideoObject", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
    public class VimeoVideo extends MediaItem {

    private static final Logger LOG = LoggerFactory.getLogger(VimeoVideo.class);


    private static final HtmlAttribute CLASS_EMBED_VIDEO_ATTR = new HtmlAttribute("class", "embed-video");
    private static final HtmlElement PLAY_BUTTON_OVERLAY = HtmlBuilders.i().withClass("fa fa-play-circle").build();
    private static final HtmlAttribute ALLOWFULLSCREEN_ATTR = new HtmlAttribute("allowfullscreen", "true");
    private static final HtmlAttribute FRAMEBORDER_ATTR = new HtmlAttribute("frameborder", "0");

    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 390;

    final MediaHelper mediaHelper = ApplicationContextHolder.getContext().getBean(MediaHelper.class);

    private String headline;

    private String vimeoId;

    private int width = DEFAULT_WIDTH;

    private int height = DEFAULT_HEIGHT;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getVimeoId() {
        return vimeoId;
    }

    public void setVimeoId(String vimeoId) {
        this.vimeoId = vimeoId;
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
        return "VimeoVideo{" +
                "url='" + getUrl() + '\'' +
                ", fileName='" + getFileName() + '\'' +
                ", fileSize=" + getFileSize() +
                ", mimeType='" + getMimeType() + '\'' +
                ", headline='" + headline + '\'' +
                ", vimeoId='" + vimeoId + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public void readFromXhtmlElement(Node xhtmlElement) {
        super.readFromXhtmlElement(xhtmlElement);
        this.setVimeoId(xhtmlElement.getAttributes().getNamedItem("data-vimeoId").getNodeValue());
        this.setHeadline(xhtmlElement.getAttributes().getNamedItem("data-headline").getNodeValue());

        this.setMvcData(getMvcData());
    }

    @Override
    public MvcData getMvcData() {
        return new MvcDataImpl("Test:Entity:VimeoVideo").defaults(MvcDataImpl.Defaults.ENTITY);
    }

    @Override
    public String toHtml(String widthFactor) {
        return toHtml(widthFactor, 0, "", 0);
    }

    @Override
    public String toHtml(String widthFactor, double aspect, String cssClass, int containerSize) {
        if (Strings.isNullOrEmpty(getUrl())) {
            return String.format(
                    "<iframe src=\"https://player.vimeo.com/video/%s?version=3&enablejsapi=1\" id=\"video%s\" class=\"%s\"/>",
                    this.getVimeoId(), UUID.randomUUID().toString().replaceAll("-", ""), null
            );
        }

        String htmlTagName = this.getIsEmbedded() ? "span" : "div";
        String placeholderImageUrl = Strings.isNullOrEmpty(getUrl()) ? null : this.mediaHelper.getResponsiveImageUrl(getUrl(), widthFactor, aspect, containerSize);

        return String.format(
                "<%s class=\"embed-video\"><img src=\"%s\" alt=\"%s\"><button type=\"button\" data-video=\"%s\" class=\"%s\"><i class=\"fa fa-play-circle\"></i></button></%s>",
                htmlTagName, placeholderImageUrl, getHeadline(), getVimeoId(), (String) null, htmlTagName
        );
    }


    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {

        if (Strings.isNullOrEmpty(this.getVimeoId())) {
            LOG.warn("Skipping Vimeo video with empty Vimeo ID: {}", this);
            return null;
        }

        return !Strings.isNullOrEmpty(this.getUrl()) ? getVimeoPlaceholder(widthFactor, aspect, cssClass, containerSize, contextPath) : getYouTubeEmbed(widthFactor, aspect, cssClass, containerSize, contextPath);
    }

    private HtmlElement getVimeoPlaceholder(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {

        final double imageAspect = aspect == 0.0 ? mediaHelper.getDefaultMediaAspect() : aspect;

        final String placeholderImageUrl = mediaHelper.getResponsiveImageUrl(this.getUrl(), widthFactor, imageAspect,
                containerSize);

        return HtmlBuilders.div()
                .withAttribute(CLASS_EMBED_VIDEO_ATTR)
                .withContent(HtmlBuilders.img(contextPath + placeholderImageUrl).withAlt(this.getHeadline()).build())
                .withContent(HtmlBuilders.button("button")
                        .withAttribute("data-video", this.getVimeoId())
                        .withClass(cssClass)
                        .withContent(PLAY_BUTTON_OVERLAY)
                        .build())
                .build();
    }

    private HtmlElement getYouTubeEmbed(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {
        return HtmlBuilders.iframe()
                .withId("video" + UUID.randomUUID().toString())
                .withAttribute("src", "https://player.vimeo.com/video/" + this.getVimeoId() + "?version=3&enablejsapi=1")
                .withAttribute(ALLOWFULLSCREEN_ATTR)
                .withAttribute(FRAMEBORDER_ATTR)
                .withClass(cssClass)
                .build();
    }
}
