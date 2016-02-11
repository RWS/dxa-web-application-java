package com.sdl.webapp.common.markup.html;

import java.util.Objects;

/**
 * <p>HtmlCommentNode class.</p>
 */
public final class HtmlCommentNode extends HtmlNode {

    private final String text;

    /**
     * <p>Constructor for HtmlCommentNode.</p>
     *
     * @param text a {@link java.lang.String} object.
     */
    public HtmlCommentNode(String text) {
        this.text = text;
    }

    /**
     * <p>Getter for the field <code>text</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getText() {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderHtml() {
        return "<!-- " + text.replaceAll("<!--", "").replaceAll("-->", "") + " -->";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlCommentNode that = (HtmlCommentNode) o;
        return Objects.equals(text, that.text);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
