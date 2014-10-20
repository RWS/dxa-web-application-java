package com.sdl.webapp.common.model.entity;

import com.sdl.webapp.common.mapping.SemanticProperties;
import com.sdl.webapp.common.mapping.SemanticProperty;

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
