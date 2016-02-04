package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>Tag class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class Tag {

    @JsonProperty("DisplayText")
    private String displayText;

    @JsonProperty("Key")
    private String key;

    @JsonProperty("TagCategory")
    private String tagCategory;

    /**
     * <p>Getter for the field <code>displayText</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * <p>Setter for the field <code>displayText</code>.</p>
     *
     * @param displayText a {@link java.lang.String} object.
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * <p>Getter for the field <code>key</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getKey() {
        return key;
    }

    /**
     * <p>Setter for the field <code>key</code>.</p>
     *
     * @param key a {@link java.lang.String} object.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * <p>Getter for the field <code>tagCategory</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTagCategory() {
        return tagCategory;
    }

    /**
     * <p>Setter for the field <code>tagCategory</code>.</p>
     *
     * @param tagCategory a {@link java.lang.String} object.
     */
    public void setTagCategory(String tagCategory) {
        this.tagCategory = tagCategory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Tag{" +
                "displayText='" + displayText + '\'' +
                ", key='" + key + '\'' +
                ", tagCategory='" + tagCategory + '\'' +
                '}';
    }
}
