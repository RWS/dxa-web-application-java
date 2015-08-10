package com.sdl.webapp.main.taglib.xpm;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlNode;

import java.util.Map;

public class XpmPropertyMarkupTag extends XpmMarkupTag {

    private static final String FIELD_PATTERN = "Start Component Field: {\"XPath\":\"%s\"}";

    private Entity entity;

    private String property;

    private int index;

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    protected HtmlNode generateXpmMarkup() {
        final Map<String, String> propertyData = entity.getPropertyData();
        if (propertyData == null) {
            return null;
        }

        final String xpath = propertyData.get(property);
        if (Strings.isNullOrEmpty(xpath)) {
            return null;
        }

        final String suffix = xpath.endsWith("]") ? "" : ("[" + (index + 1) + "]");

        return new HtmlCommentNode(String.format(FIELD_PATTERN, xpath + suffix));
    }
}
