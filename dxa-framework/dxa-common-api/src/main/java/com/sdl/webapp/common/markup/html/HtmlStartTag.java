package com.sdl.webapp.common.markup.html;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * <p>HtmlStartTag class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class HtmlStartTag extends HtmlRenderable {

    private final String tagName;

    private final List<HtmlAttribute> attributes;

    /**
     * <p>Constructor for HtmlStartTag.</p>
     *
     * @param tagName    a {@link java.lang.String} object.
     * @param attributes a {@link java.util.List} object.
     */
    public HtmlStartTag(String tagName, List<HtmlAttribute> attributes) {
        this.tagName = tagName;
        this.attributes = ImmutableList.copyOf(attributes);
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
     * <p>Getter for the field <code>attributes</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<HtmlAttribute> getAttributes() {
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderHtml() {
        if (StringUtils.isEmpty(tagName)) {
            return "";
        }

        final StringBuilder sb = new StringBuilder(16).append('<').append(tagName);
        for (HtmlAttribute attribute : attributes) {
            sb.append(' ').append(attribute.toHtml());
        }
        return sb.append('>').toString();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlStartTag that = (HtmlStartTag) o;
        return Objects.equals(tagName, that.tagName) &&
                Objects.equals(attributes, that.attributes);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(tagName, attributes);
    }
}
