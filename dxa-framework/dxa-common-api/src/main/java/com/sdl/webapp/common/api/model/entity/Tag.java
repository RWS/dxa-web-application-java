package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tag {

    @JsonProperty("DisplayText")
    private String displayText;

    @JsonProperty("Key")
    private String key;

    @JsonProperty("TagCategory")
    private String tagCategory;

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTagCategory() {
        return tagCategory;
    }

    public void setTagCategory(String tagCategory) {
        this.tagCategory = tagCategory;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "displayText='" + displayText + '\'' +
                ", key='" + key + '\'' +
                ", tagCategory='" + tagCategory + '\'' +
                '}';
    }
}
