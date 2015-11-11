package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.MvcDataImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.img;
import static org.springframework.util.StringUtils.isEmpty;

@SemanticEntity(entityName = "ImageObject", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class Image extends MediaItem {

    private static final Logger LOG = LoggerFactory.getLogger(Image.class);
    final MediaHelper mediaHelper = ApplicationContextHolder.getContext().getBean(MediaHelper.class);
    @SemanticProperty("s:name")
    @JsonProperty("AlternateText")
    private String alternateText;

    public String getAlternateText() {
        return alternateText;
    }

    public void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

    @Override
    @JsonIgnore
    public boolean isImage() {
        return true;
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor) throws DxaException {
        return this.toHtmlElement(widthFactor, 0, "", 0);
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize) throws DxaException {
        return toHtmlElement(widthFactor, aspect, cssClass, containerSize, "");
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) throws DxaException {
        if (isEmpty(getUrl())) {
            LOG.warn("Skipping image with empty URL: {}", this);
            throw new DxaException("URL is null for image component: " + this);
        }

        return img(mediaHelper.getResponsiveImageUrl(getUrl(), widthFactor, aspect, containerSize))
                .withAlt(getAlternateText())
                .withClass(cssClass)
                .withAttribute("data-aspect", String.valueOf((Math.round(aspect * 100) / 100)))
                .withAttribute("width", widthFactor)
                .build();
    }

    @Override
    public void readFromXhtmlElement(Node xhtmlElement) {
        super.readFromXhtmlElement(xhtmlElement);

        this.setAlternateText(xhtmlElement.getAttributes().getNamedItem("alt").getNodeValue());
        this.setMvcData(getMvcData());
    }

    @Override
    public MvcData getMvcData() {
        return new MvcDataImpl("Core:Entity:Image").defaults(MvcDataImpl.Defaults.ENTITY);
    }

    @Override
    public String toString() {
        return "Image{" +
                "url='" + getUrl() + '\'' +
                ", fileName='" + getFileName() + '\'' +
                ", fileSize=" + getFileSize() +
                ", mimeType='" + getMimeType() + '\'' +
                ", alternateText='" + alternateText + '\'' +
                '}';
    }
}
