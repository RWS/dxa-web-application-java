package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

@EqualsAndHashCode(callSuper = true)
@Data
@SemanticEntity(entityName = "VideoObject", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class YouTubeVideo extends MediaItem {

    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 390;

    @JsonProperty("Headline")
    private String headline;

    @JsonProperty("YouTubeId")
    private String youTubeId;

    @JsonProperty("Width")
    private int width = DEFAULT_WIDTH;

    @JsonProperty("Height")
    private int height = DEFAULT_HEIGHT;

    @Override
    public void readFromXhtmlElement(Node xhtmlElement) {
        super.readFromXhtmlElement(xhtmlElement);
        this.setYouTubeId(xhtmlElement.getAttributes().getNamedItem("data-youTubeId").getNodeValue());
        this.setHeadline(xhtmlElement.getAttributes().getNamedItem("data-headline").getNodeValue());

        this.setMvcData(getMvcData());
    }

    @Override
    public MvcData getMvcData() {
        return MvcDataCreator.creator()
                .fromQualifiedName("Core:Entity:YouTubeVideo")
                .defaults(DefaultsMvcData.CORE_ENTITY)
                .create();
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

        double imageAspect = (aspect == 0.0) ? getMediaHelper().getDefaultMediaAspect() : aspect;

        String placeholderImageUrl = getMediaHelper().getResponsiveImageUrl(this.getUrl(), widthFactor, imageAspect, containerSize);

        final HtmlElement playButtonOverlay = i().withClass("fa fa-play-circle").build();

        return (isEmbedded() ? span() : div())
                .withAttribute("class", "embed-video")
                .withNode(
                        img(contextPath + placeholderImageUrl).withAlt(this.headline).build())
                .withNode(
                        button("button")
                                .withAttribute("data-video", this.youTubeId)
                                .withClass(cssClass)
                                .withNode(playButtonOverlay)
                                .build())
                .build();
    }

    private HtmlElement getYouTubeEmbed(String cssClass) {
        return iframe()
                .withId("video" + UUID.randomUUID().toString())
                .withAttribute("src", "https://www.youtube.com/embed/" + this.youTubeId + "?version=3&enablejsapi=1")
                .withAttribute("allowfullscreen", "true")
                .withAttribute("frameborder", "0")
                .withClass(cssClass)
                .build();
    }


}
