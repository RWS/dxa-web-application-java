package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class XpmEntityMarkupTag extends XpmMarkupTag {

    private static final String COMPONENT_PRESENTATION_PATTERN = "Start Component Presentation: " +
            "{\"ComponentID\":\"%s\",\"ComponentModified\":\"%s\",\"ComponentTemplateID\":\"%s\"," +
            "\"ComponentTemplateModified\":\"%s\",\"IsRepositoryPublished\":%s}";

    private EntityModel entity;

    public void setEntity(EntityModel entity) {
        this.entity = entity;
    }

    @Override
    public HtmlNode generateXpmMarkup() {
        return new HtmlNode() {
            @Override
            protected String renderHtml() {
                return entity.getXpmMarkup(getLocalization());
            }
        };
       /* final Map<String, String> entityData = entity.getXpmMetadata();

        final String componentId = entity instanceof EclItem ? ((EclItem) entity).getUri() : entityData.get("ComponentID");
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
                */

    }

    private Localization getLocalization() {
        if(pageContext == null)
            return null;
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getLocalization();
    }
}
