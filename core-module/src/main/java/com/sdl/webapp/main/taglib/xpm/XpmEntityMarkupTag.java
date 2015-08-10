package com.sdl.webapp.main.taglib.xpm;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlNode;

import java.util.Map;

public class XpmEntityMarkupTag extends XpmMarkupTag {

    private static final String COMPONENT_PRESENTATION_PATTERN = "Start Component Presentation: " +
            "{\"ComponentID\":\"%s\",\"ComponentModified\":\"%s\",\"ComponentTemplateID\":\"%s\"," +
            "\"ComponentTemplateModified\":\"%s\",\"IsRepositoryPublished\":%s}";

    private Entity entity;

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public HtmlNode generateXpmMarkup() {
        final Map<String, String> entityData = entity.getEntityData();

        final String componentId = entityData.get("ComponentID");
        if (Strings.isNullOrEmpty(componentId)) {
            return null;
        }

        final String componentModified = entityData.get("ComponentModified");
        final String templateId = entityData.get("ComponentTemplateID");
        final String templateModified = entityData.get("ComponentTemplateModified");

        final String isRepositoryPublished;
        if (templateId.equals("tcm:0-0-0")) {
            isRepositoryPublished = "true,\"IsQueryBased\":true";
        } else {
            isRepositoryPublished = "false";
        }

        return new HtmlCommentNode(String.format(COMPONENT_PRESENTATION_PATTERN,
                componentId, componentModified, templateId, templateModified, isRepositoryPublished));
    }
}
