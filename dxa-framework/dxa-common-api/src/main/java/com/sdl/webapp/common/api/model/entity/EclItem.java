package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.div;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.empty;
import static java.lang.String.format;
import static org.springframework.util.StringUtils.isEmpty;

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

    public Object getFromExternalMetadataOrAlternative(String key, Object alternative) {
        final Object obj = new NestedStringMap(getExternalMetadata()).get(key);
        return obj == null ? alternative : obj;
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor) throws DxaException {
        return toHtmlElement(widthFactor, 0.0, null, 0);
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
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize) throws DxaException {
        return toHtmlElement(widthFactor, aspect, cssClass, containerSize, "");
    }

    /**
     * Returns an HTML representation.
     *
     * @param widthFactor   The factor to apply to the width - can be % (eg "100%") or absolute (eg "120") | <strong>ignored</strong>
     * @param aspect        The aspect ratio to apply | <strong>ignored</strong>
     * @param cssClass      Optional CSS class name(s) to apply
     * @param containerSize The size (in grid column units) of the containing element | <strong>ignored</strong>
     * @param contextPath Context path | <strong>ignored</strong>
     * @return string html representation
     */
    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) throws DxaException {
        if (isEmpty(templateFragment)) {
            throw new DxaException(format("Attempt to render an ECL Item for which no Template Fragment is available: " +
                            "'%s' (DisplayTypeId: '%s')", getUri(), getDisplayTypeId()));
        }

        if (isEmpty(cssClass)) {
            return empty().withPureHtmlContent(templateFragment).build();
        }

        return div().withClass(cssClass).withPureHtmlContent(templateFragment).build();
    }

    @Override
    public String getXpmMarkup(Localization localization) {
        if (getXpmMetadata() != null && !isEmpty(this.uri)) {
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
        if (!isEmpty(eclFileName)) {
            this.setFileName(eclFileName);
        }

        String eclMimeType = attributes.getNamedItem("data-eclMimeType").getNodeValue();
        if (!isEmpty(eclMimeType)) {
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

    public static class NestedStringMap {
        private Map<String, Object> map;

        public NestedStringMap(Map<String, Object> map) {
            this.map = map;
        }

        public Map<String, Object> getMap() {
            return map;
        }

        public Object get(String key) {
            if (key == null || CollectionUtils.isEmpty(map)) {
                return null;
            }

            final List<String> keys = Arrays.asList(key.split("/"));
            final Iterator<String> iterator = keys.iterator();
            Map currentMap = map;
            while (iterator.hasNext()) {
                String current = iterator.next();
                if (!iterator.hasNext()) { //last element
                    return currentMap.get(current);
                }

                currentMap = MapUtils.getMap(currentMap, current);
                if (currentMap == null) {
                    return null;
                }
            }

            return null;
        }
    }
}
