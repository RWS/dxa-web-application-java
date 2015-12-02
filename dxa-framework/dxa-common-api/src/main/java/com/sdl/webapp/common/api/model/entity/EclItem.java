package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.util.FieldUtils;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.div;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.empty;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;

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
        final Object obj = new NestedCustomMap(getExternalMetadata(),
                new Function<Entry<String, Map<String, Object>>, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(Entry<String, Map<String, Object>> pair) {
                String key = pair.getKey();
                Map<String, Object> map = pair.getValue();

                Object o = map.get(key);
                Map content;
                if (o instanceof FieldSet) {
                    content = ((FieldSet) o).getContent();
                } else if (o instanceof EmbeddedField) {
                    content = ((EmbeddedField) o).getEmbeddedValues().get(0).getContent();
                } else {
                    throw new UnsupportedOperationException("Unsupported format of ECL metadata structure");
                }
                //noinspection unchecked
                return content;
            }
        }).get(key);

        if (obj == null) {
            return alternative;
        }

        if (obj instanceof Field) {
            return FieldUtils.getStringValue((Field) obj);
        }

        return alternative;
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
     * @param contextPath   Context path | <strong>ignored</strong>
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

    private String getNodeValue(NamedNodeMap attributes, String name) {
        final Node namedItem = attributes.getNamedItem(name);
        return namedItem != null ? namedItem.getNodeValue() : null;
    }

    @Override
    public String toString() {
        return "EclItem{" +
                "uri='" + uri + '\'' +
                ", displayTypeId='" + displayTypeId + '\'' +
                ", templateFragment='" + templateFragment + '\'' +
                '}';
    }

    public static class NestedCustomMap {
        private Map<String, Object> map;
        private Function<Entry<String, Map<String, Object>>, Map<String, Object>> loadNestedFunction;

        public NestedCustomMap(Map<String, Object> map,
                               Function<Entry<String, Map<String, Object>>, Map<String, Object>> function) {
            this.map = map;
            this.loadNestedFunction = function;
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
            Map<String, Object> currentMap = map;
            while (iterator.hasNext()) {
                String current = iterator.next();
                if (!iterator.hasNext()) { //last element
                    return currentMap.get(current);
                }

                currentMap = loadNestedFunction.apply(Maps.immutableEntry(current, currentMap));
                if (currentMap == null) {
                    return null;
                }
            }

            return null;
        }
    }
}
