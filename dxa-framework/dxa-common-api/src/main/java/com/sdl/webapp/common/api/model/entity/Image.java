package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Node;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.img;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@SemanticEntity(entityName = "ImageObject", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class Image extends MediaItem {

    @SemanticProperty("s:name")
    @JsonProperty("AlternateText")
    private String alternateText;

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public boolean isImage() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlElement toHtmlElement(String widthFactor) throws DxaException {
        return this.toHtmlElement(widthFactor, 0, "", 0);
    }

    /** {@inheritDoc} */
    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize) throws DxaException {
        return toHtmlElement(widthFactor, aspect, cssClass, containerSize, "");
    }

    /** {@inheritDoc} */
    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) throws DxaException {
        if (isEmpty(getUrl())) {
            log.warn("Skipping image with empty URL: {}", this);
            throw new DxaException("URL is null for image component: " + this);
        }

        return img(getMediaHelper().getResponsiveImageUrl(getUrl(), widthFactor, aspect, containerSize))
                .withAlt(this.alternateText)
                .withClass(cssClass)
                .withAttribute("data-aspect", String.valueOf(Math.round(aspect * 100) / 100))
                .withAttribute("width", widthFactor)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public void readFromXhtmlElement(Node xhtmlElement) {
        super.readFromXhtmlElement(xhtmlElement);

        this.alternateText = xhtmlElement.getAttributes().getNamedItem("alt").getNodeValue();
        this.setMvcData(getMvcData());
    }

    /** {@inheritDoc} */
    @Override
    public MvcData getMvcData() {
        return MvcDataCreator.creator()
                .fromQualifiedName("Core:Entity:Image")
                .defaults(DefaultsMvcData.CORE_ENTITY)
                .create();
    }
}
