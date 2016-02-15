package com.sdl.webapp.common.markup.html;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * <p>HtmlEndTag class.</p>
 */
public final class HtmlEndTag extends HtmlRenderable {

    private final String tagName;

    /**
     * <p>Constructor for HtmlEndTag.</p>
     *
     * @param tagName a {@link java.lang.String} object.
     */
    public HtmlEndTag(String tagName) {
        this.tagName = tagName;
    }

    /**
     * <p>Getter for the field <code>tagName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderHtml() {
        return StringUtils.isEmpty(tagName) ? "" : "</" + tagName + '>';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlEndTag that = (HtmlEndTag) o;
        return Objects.equals(tagName, that.tagName);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(tagName);
    }
}
