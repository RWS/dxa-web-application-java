package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping2.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping2.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping2.config.Vocabularies;

@SemanticEntity(entityName = "MediaObject", vocabulary = Vocabularies.SCHEMA_ORG, prefix = "s")
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
