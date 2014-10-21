package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.SemanticProperties;
import com.sdl.webapp.common.api.mapping.SemanticProperty;

public class TagLink extends EntityBase {

    @SemanticProperties({
            @SemanticProperty("internalLink"),
            @SemanticProperty("externalLink")
    })
    private String url;

    private Tag tag;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
