package com.sdl.webapp.common.impl.taglib.xpm;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlNode;

import java.util.Map;

/**
 * <p>XpmPropertyMarkupTag class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class XpmPropertyMarkupTag extends XpmMarkupTag {

    private static final String FIELD_PATTERN = "Start Component Field: {\"XPath\":\"%s\"}";

    private EntityModel entity;

    private String property;

    private int index;

    /**
     * <p>Setter for the field <code>entity</code>.</p>
     *
     * @param entity a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     */
    public void setEntity(EntityModel entity) {
        this.entity = entity;
    }

    /**
     * <p>Setter for the field <code>property</code>.</p>
     *
     * @param property a {@link java.lang.String} object.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * <p>Setter for the field <code>index</code>.</p>
     *
     * @param index a int.
     */
    public void setIndex(int index) {
        this.index = index;
    }

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
