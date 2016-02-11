package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import com.sdl.webapp.common.util.Dd4tUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Map;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.div;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.empty;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * <p>Abstract EclItem class.</p>
 */
@SemanticEntity(entityName = "ExternalContentItem", vocabulary = SDL_CORE, prefix = "s")
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
     * <p>getFromExternalMetadataOrAlternative.</p>
     *
     * @param externalMetadata a {@link java.util.Map} object.
     * @param key              a {@link java.lang.String} object.
     * @param alternative      a {@link java.lang.Object} object.
     * @return a {@link java.lang.Object} object.
     */
    public static Object getFromExternalMetadataOrAlternative(Map<String, Object> externalMetadata, String key, Object alternative) {
        Dd4tUtils dd4tUtils = ApplicationContextHolder.getContext().getBean(Dd4tUtils.class);
        return dd4tUtils.getFromNestedMultiLevelMapOrAlternative(externalMetadata, key, alternative);
    }

    private static String getNodeValue(NamedNodeMap attributes, String name) {
        final Node namedItem = attributes.getNamedItem(name);
        return namedItem != null ? namedItem.getNodeValue() : null;
    }

    /**
     * <p>Getter for the field <code>uri</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * <p>Setter for the field <code>uri</code>.</p>
     *
     * @param uri a {@link java.lang.String} object.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * <p>Getter for the field <code>displayTypeId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDisplayTypeId() {
        return displayTypeId;
    }

    /**
     * <p>Setter for the field <code>displayTypeId</code>.</p>
     *
     * @param displayTypeId a {@link java.lang.String} object.
     */
    public void setDisplayTypeId(String displayTypeId) {
        this.displayTypeId = displayTypeId;
    }

    /**
     * <p>Getter for the field <code>templateFragment</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTemplateFragment() {
        return templateFragment;
    }

    /**
     * <p>Setter for the field <code>templateFragment</code>.</p>
     *
     * @param templateFragment a {@link java.lang.String} object.
     */
    public void setTemplateFragment(String templateFragment) {
        this.templateFragment = templateFragment;
    }

    /**
     * External metadata map for {@link com.sdl.webapp.common.api.model.entity.EclItem}.
     * <p>
     * Keys are the field names. Values can be simple types (String, Double, DateTime), nested Maps.
     * </p>
     *
     * @return external metadata
     */
    public Map<String, Object> getExternalMetadata() {
        return externalMetadata;
    }

    /**
     * <p>Setter for the field <code>externalMetadata</code>.</p>
     *
     * @param externalMetadata a {@link java.util.Map} object.
     */
    public void setExternalMetadata(Map<String, Object> externalMetadata) {
        this.externalMetadata = externalMetadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlElement toHtmlElement(String widthFactor) throws DxaException {
        return toHtmlElement(widthFactor, 0.0, null, 0);
    }

    /**
     * {@inheritDoc}
     *
     * Returns an HTML representation.
     */
    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize) throws DxaException {
        return toHtmlElement(widthFactor, aspect, cssClass, containerSize, "");
    }

    /**
     * {@inheritDoc}
     *
     * Returns an HTML representation.
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

    /** {@inheritDoc} */
    @Override
    public String getXpmMarkup(Localization localization) {
        if (getXpmMetadata() != null && !isEmpty(this.uri)) {
            getXpmMetadata().put(COMPONENT_ID_KEY, this.uri);
        }
        return super.getXpmMarkup(localization);
    }

    /** {@inheritDoc} */
    @Override
    public void readFromXhtmlElement(Node xhtmlElement) {
        super.readFromXhtmlElement(xhtmlElement);
        NamedNodeMap attributes = xhtmlElement.getAttributes();

        if (attributes == null) {
            return;
        }

        this.uri = getNodeValue(attributes, "data-eclId");
        this.displayTypeId = getNodeValue(attributes, "data-eclDisplayTypeId");
        this.templateFragment = getNodeValue(attributes, "data-eclTemplateFragment");

        // Note that FileName and MimeType are already set in MediaItem.ReadFromXhtmlElement.
        // We overwrite those with the values provided by ECL (if any).
        String eclFileName = getNodeValue(attributes, "data-eclFileName");
        if (!isEmpty(eclFileName)) {
            this.setFileName(eclFileName);
        }

        String eclMimeType = getNodeValue(attributes, "data-eclMimeType");
        if (!isEmpty(eclMimeType)) {
            this.setMimeType(eclMimeType);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "EclItem{" +
                "uri='" + uri + '\'' +
                ", displayTypeId='" + displayTypeId + '\'' +
                ", templateFragment='" + templateFragment + '\'' +
                '}';
    }

}
