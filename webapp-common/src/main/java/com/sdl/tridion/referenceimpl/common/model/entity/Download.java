package com.sdl.tridion.referenceimpl.common.model.entity;

import com.sdl.tridion.referenceimpl.common.mapping.SemanticEntity;
import com.sdl.tridion.referenceimpl.common.mapping.SemanticProperties;
import com.sdl.tridion.referenceimpl.common.mapping.SemanticProperty;

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
