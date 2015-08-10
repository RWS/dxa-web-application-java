package com.sdl.webapp.common.api.model.entity;

public class Tag {

    private String displayText;

    private String key;

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
