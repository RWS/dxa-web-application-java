package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dd4t.contentmodel.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all tridion items
 *
 * The latest DD4T version has uppercase start characters in elements.
 *
 * @author Quirijn Slings, Raimond Kempees
 */
public abstract class BaseItem implements Item {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("CustomProperties")
    private Map<String, Object> customProperties = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, Object> customProperties) {
        this.customProperties = customProperties;
    }

    public void addCustomProperty(String key, Object value) {
        customProperties.put(key, value);
    }

    public Object getCustomProperty(String key) {
        return customProperties.get(key);
    }
}