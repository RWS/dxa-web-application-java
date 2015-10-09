package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.markup.html.HtmlElement;
import org.springframework.util.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Map;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

/**
 * ECL Item
 *
 * @author nic
 */
@SemanticEntity(entityName = "ExternalContentItem", vocabulary = SDL_CORE, prefix = "s")
public abstract class EclItem extends MediaItem {

    static final String COMPONENT_ID_KEY = "ComponentID";

    private String uri;
    private String displayTypeId;
    private String templateFragment;
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
     *
     * Keys are the field names. Values can be simple types (String, Double, DateTime), nested Maps.
     */
    public Map<String, Object> getExternalMetadata() {
        return externalMetadata;
    }

    public void setExternalMetadata(Map<String, Object> externalMetadata) {
        this.externalMetadata = externalMetadata;
    }

    @Override
    public String toHtml(String widthFactor) {
        // NOTE: params will be ignored
        return toHtml(widthFactor, 0.0, null, 0);
    }

    @Override
    public String toHtml(String widthFactor, double aspect, String cssClass, int containerSize) {
        // NOTE: we're ignoring all parameters here.
        return templateFragment;
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {
        // TODO implement this functionality
        throw new UnsupportedOperationException("This should be implemented in a subclass of EclItem");
    }

    @Override
    public String getXpmMarkup(Localization localization)
    {
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
