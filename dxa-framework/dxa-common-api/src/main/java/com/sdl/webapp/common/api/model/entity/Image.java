package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.MvcDataImpl;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.Locale;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;

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
    public String toHtml(String widthFactor) {
        return this.toHtml(widthFactor, 0, "", 0);
    }

    @Override
    public String toHtml(String widthFactor, double aspect, String cssClass, int containerSize) {
        String responsiveImageUrl = this.mediaHelper.getResponsiveImageUrl(getUrl(), widthFactor, aspect, containerSize);
        String dataAspect = String.valueOf((Math.round(aspect * 100) / 100));
        String widthAttr = Strings.isNullOrEmpty(widthFactor) ? null : String.format("width=\"%s\"", widthFactor);
        String classAttr = Strings.isNullOrEmpty(cssClass) ? null : String.format("class=\"%s\"", cssClass);
        return String.format("<img src=\"%s\" alt=\"%s\" data-aspect=\"%s\" %s%s/>",
                responsiveImageUrl, getAlternateText(), dataAspect, widthAttr, classAttr);
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {

        if (Strings.isNullOrEmpty(this.getUrl())) {
            LOG.warn("Skipping image with empty URL: {}", this);
            return null;
        }

        String imgWidth = widthFactor;

        if (Strings.isNullOrEmpty(widthFactor)) {
            widthFactor = mediaHelper.getDefaultMediaFill();
        }

        final String imageUrl = mediaHelper.getResponsiveImageUrl(this.getUrl(), widthFactor, aspect, containerSize);

        return HtmlBuilders.img(contextPath + imageUrl)
                .withAlt(this.getAlternateText())
                .withClass(cssClass)
                .withWidth(imgWidth)
                .withAttribute("data-aspect", String.format(Locale.US, "%.2f", aspect))
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
