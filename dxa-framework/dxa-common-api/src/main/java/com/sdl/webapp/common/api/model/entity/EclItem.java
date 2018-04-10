package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Node;

import java.util.Map;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.div;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.empty;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * @dxa.publicApi
 */
@SemanticEntity(entityName = "ExternalContentItem", vocabulary = SDL_CORE, prefix = "s")
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class EclItem extends MediaItem {

    static final String COMPONENT_ID_KEY = "ComponentID";

    @JsonProperty("EclUri")
    private String uri;

    @JsonProperty("EclDisplayTypeId")
    private String displayTypeId;

    @JsonProperty("EclTemplateFragment")
    private String templateFragment;

    @JsonProperty("EclExternalMetadata")
    private Map<String, Object> externalMetadata;

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlElement toHtmlElement(String widthFactor) throws DxaException {
        return toHtmlElement(widthFactor, 0.0, null, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize) throws DxaException {
        return toHtmlElement(widthFactor, aspect, cssClass, containerSize, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) throws DxaException {
        if (isEmpty(templateFragment)) {
            throw new DxaException(format("Attempt to render an ECL Item for which no Template Fragment is available: " +
                    "'%s' (DisplayTypeId: '%s')", this.uri, displayTypeId));
        }

        if (isEmpty(cssClass)) {
            return empty().withPureHtmlContent(templateFragment).build();
        }

        return div().withClass(cssClass).withPureHtmlContent(templateFragment).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readFromXhtmlElement(Node xhtmlElement) {
        super.readFromXhtmlElement(xhtmlElement);

        setUri(getNodeAttribute(xhtmlElement, "data-eclId"));
        setDisplayTypeId(getNodeAttribute(xhtmlElement, "data-eclDisplayTypeId"));
        setTemplateFragment(getNodeAttribute(xhtmlElement, "data-eclTemplateFragment"));

        // Note that FileName and MimeType are already set in MediaItem.ReadFromXhtmlElement.
        // We overwrite those with the values provided by ECL (if any).
        String eclFileName = getNodeAttribute(xhtmlElement, "data-eclFileName");
        if (!isEmpty(eclFileName)) {
            setFileName(eclFileName);
        }

        String eclMimeType = getNodeAttribute(xhtmlElement, "data-eclMimeType");
        if (!isEmpty(eclMimeType)) {
            setMimeType(eclMimeType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXpmMarkup(Localization localization) {
        if (getXpmMetadata() != null && !isEmpty(this.uri)) {
            getXpmMetadata().put(COMPONENT_ID_KEY, this.uri);
        }
        return super.getXpmMarkup(localization);
    }

}
