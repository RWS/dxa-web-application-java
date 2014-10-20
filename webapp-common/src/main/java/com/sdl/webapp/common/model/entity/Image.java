package com.sdl.webapp.common.model.entity;

import com.sdl.webapp.common.mapping.SemanticEntity;
import com.sdl.webapp.common.mapping.SemanticProperty;

@SemanticEntity(entityName = "MediaObject", vocab = "http://schema.org", prefix = "s", pub = true)
public class Image extends MediaItem {

    @SemanticProperty("s:name")
    private String alternateText;

    public String getAlternateText() {
        return alternateText;
    }

    public void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }
}
