package org.dd4t.contentmodel;

import java.util.Map;

/**
 * Top interface for all items in tridion.
 *
 * @author Quirijn Slings
 */
public interface Item {

    /**
     * Get the tridion id.
     *
     * @return the tridion id i.e. tcm:1-1-32
     */
    public String getId();

    /**
     * Set the id
     *
     * @param id
     */
    public void setId(String id);

    /**
     * Get the title
     *
     * @return
     */
    public String getTitle();

    /**
     * Set the title
     *
     * @param title
     */
    public void setTitle(String title);

    /**
     * Add a custom property
     *
     * @param key
     * @param value
     */
    public void addCustomProperty(String key, Object value);

    /**
     * Get a custom property
     *
     * @param key
     * @return the property object
     */
    public Object getCustomProperty(String key);

    /**
     * Get the Map of custom properties
     *
     * @return the map of custom properties
     */
    public Map<String, Object> getCustomProperties();

    /**
     * Set the map of custom properties
     *
     * @param customProperties
     */
    public void setCustomProperties(Map<String, Object> customProperties);
}
