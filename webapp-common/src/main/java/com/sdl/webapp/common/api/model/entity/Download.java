package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.SemanticEntity;
import com.sdl.webapp.common.api.mapping.SemanticProperties;
import com.sdl.webapp.common.api.mapping.SemanticProperty;

@SemanticEntity(entityName = "MediaObject", vocab = "http://schema.org", prefix = "s", pub = true)
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
