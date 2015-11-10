package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import org.springframework.util.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Map;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

/**
 * ECL Item
 *
 * @author nic
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

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDisplayTypeId() {
        return displayTypeId;
    }

    public void setDisplayTypeId(String displayTypeId) {
        this.displayTypeId = displayTypeId;
    }

    public String getTemplateFragment() {
        return templateFragment;
    }

    public void setTemplateFragment(String templateFragment) {
        this.templateFragment = templateFragment;
    }

    /**
     * External metadata map for {@link EclItem}.
     * <p/>
     * Keys are the field names. Values can be simple types (String, Double, DateTime), nested Maps.
     */
    public Map<String, Object> getExternalMetadata() {
        return externalMetadata;
    }

    public void setExternalMetadata(Map<String, Object> externalMetadata) {
        this.externalMetadata = externalMetadata;
    }

    @Override
    public String toHtml(String widthFactor) throws DxaException {
        return toHtml(widthFactor, 0.0, null, 0);
    }

    /**
     * Returns an HTML representation.
     *
     * @param widthFactor   The factor to apply to the width - can be % (eg "100%") or absolute (eg "120") | <strong>ignored</strong>
     * @param aspect        The aspect ratio to apply | <strong>ignored</strong>
     * @param cssClass      Optional CSS class name(s) to apply
     * @param containerSize The size (in grid column units) of the containing element | <strong>ignored</strong>
     * @return string html representation
     */
    @Override
    public String toHtml(String widthFactor, double aspect, String cssClass, int containerSize) throws DxaException {
        if (StringUtils.isEmpty(templateFragment)) {
            throw new DxaException(
                    String.format("Attempt to render an ECL Item for which no Template Fragment is available: " +
                            "'%s' (DisplayTypeId: '%s')", getUri(), getDisplayTypeId()));
        }

        if (StringUtils.isEmpty(cssClass)) {
            return templateFragment;
        }

        return String.format("<div class=\"%s\">%s</div>", cssClass, templateFragment);
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {
        // TODO implement this functionality
        throw new UnsupportedOperationException("This should be implemented in a subclass of EclItem");
    }

    @Override
    public String getXpmMarkup(Localization localization) {
        if (getXpmMetadata() != null && !StringUtils.isEmpty(this.uri)) {
            getXpmMetadata().put(COMPONENT_ID_KEY, this.uri);
        }
        return super.getXpmMarkup(localization);
    }

    @Override
    public void readFromXhtmlElement(Node xhtmlElement) {
        super.readFromXhtmlElement(xhtmlElement);
        NamedNodeMap attributes = xhtmlElement.getAttributes();

        if (attributes == null) {
            return;
        }

        this.uri = attributes.getNamedItem("data-eclId").getNodeValue();
        this.displayTypeId = attributes.getNamedItem("data-eclDisplayTypeId").getNodeValue();
        this.templateFragment = attributes.getNamedItem("data-eclTemplateFragment").getNodeValue();

        // Note that FileName and MimeType are already set in MediaItem.ReadFromXhtmlElement.
        // We overwrite those with the values provided by ECL (if any).
        String eclFileName = attributes.getNamedItem("data-eclFileName").getNodeValue();
        if (!StringUtils.isEmpty(eclFileName)) {
            this.setFileName(eclFileName);
        }

        String eclMimeType = attributes.getNamedItem("data-eclMimeType").getNodeValue();
        if (!StringUtils.isEmpty(eclMimeType)) {
            this.setMimeType(eclMimeType);
        }
    }

    @Override
    public String toString() {
        return "EclItem{" +
                "uri='" + uri + '\'' +
                ", displayTypeId='" + displayTypeId + '\'' +
                ", templateFragment='" + templateFragment + '\'' +
                '}';
    }
}
