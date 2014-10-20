package com.sdl.webapp.common.model.entity;

import com.sdl.webapp.common.mapping.SemanticEntity;
import com.sdl.webapp.common.model.Entity;

@SemanticEntity(entityName = "NotificationBar", vocab = Entity.CORE_VOCABULARY, prefix = "nb")
public class Notification extends EntityBase {

    private String headline;
    private String text;
    private String continue_;
    private Link link;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContinue() {
        return continue_;
    }

    public void setContinue(String continue_) {
        this.continue_ = continue_;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
