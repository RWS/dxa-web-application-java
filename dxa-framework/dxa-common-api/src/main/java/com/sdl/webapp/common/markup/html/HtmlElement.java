package com.sdl.webapp.common.markup.html;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

/**
 * <p>HtmlElement class.</p>
 */
public final class HtmlElement extends HtmlNode {

    private final HtmlStartTag startTag;

    private final List<HtmlNode> content;

    private final HtmlEndTag endTag;

    /**
     * <p>Constructor for HtmlElement.</p>
     *
     * @param tagName    a {@link java.lang.String} object.
     * @param closeTag   a boolean.
     * @param attributes a {@link java.util.List} object.
     * @param content    a {@link java.util.List} object.
     */
    public HtmlElement(String tagName, boolean closeTag, List<HtmlAttribute> attributes, List<HtmlNode> content) {
        this.startTag = new HtmlStartTag(tagName, attributes);
        this.content = ImmutableList.copyOf(content);
        this.endTag = closeTag ? new HtmlEndTag(tagName) : null;
    }

    /**
     * <p>Getter for the field <code>startTag</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.HtmlStartTag} object.
     */
    public HtmlStartTag getStartTag() {
        return startTag;
    }

    /**
     * <p>Getter for the field <code>endTag</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.HtmlEndTag} object.
     */
    public HtmlEndTag getEndTag() {
        return endTag;
    }

    /**
     * <p>Getter for the field <code>content</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<HtmlNode> getContent() {
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderHtml() {
        final StringBuilder sb = new StringBuilder(1024).append(startTag.toHtml());
        for (HtmlNode node : content) {
            sb.append(node.toHtml());
        }
        if (endTag != null) {
            sb.append(endTag.toHtml());
        }
        return sb.toString();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlElement that = (HtmlElement) o;
        return Objects.equals(startTag, that.startTag) &&
                Objects.equals(content, that.content) &&
                Objects.equals(endTag, that.endTag);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(startTag, content, endTag);
    }
}
