package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping2.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping2.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping2.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping2.config.Vocabularies;

@SemanticEntity(entityName = "MediaObject", vocabulary = Vocabularies.SCHEMA_ORG, prefix = "s")
public class Download extends MediaItem {

    @SemanticProperties({
            @SemanticProperty("s:name"),
            @SemanticProperty("s:description")
    })
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
