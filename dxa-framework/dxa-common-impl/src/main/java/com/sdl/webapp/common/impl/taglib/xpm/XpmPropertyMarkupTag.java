package com.sdl.webapp.common.impl.taglib.xpm;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import lombok.Setter;

import java.util.Map;

@Setter
public class XpmPropertyMarkupTag extends XpmMarkupTag {

    private static final String FIELD_PATTERN = "Start Component Field: {\"XPath\":\"%s\"}";

    private EntityModel entity;

    private String property;

    private int index;

    /**
     * {@inheritDoc}
     */
    @Override
    protected HtmlNode generateXpmMarkup() {
        final Map<String, String> propertyData = entity.getXpmPropertyMetadata();
        if (propertyData == null) {
            return null;
        }

        final String xpath = propertyData.get(property);
        if (Strings.isNullOrEmpty(xpath)) {
            return null;
        }

        final String suffix = xpath.endsWith("]") ? "" : ("[" + (index + 1) + ']');

        return new HtmlCommentNode(String.format(FIELD_PATTERN, xpath + suffix));
    }
}
