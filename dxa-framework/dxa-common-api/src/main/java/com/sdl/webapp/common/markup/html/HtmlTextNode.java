package com.sdl.webapp.common.markup.html;

import org.springframework.web.util.HtmlUtils;

import java.util.Objects;

/**
 * <p>HtmlTextNode class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class HtmlTextNode extends HtmlNode {

    private final String text;

    private final boolean escape;

    /**
     * <p>Constructor for HtmlTextNode.</p>
     *
     * @param text   a {@link java.lang.String} object.
     * @param escape a boolean.
     */
    public HtmlTextNode(String text, boolean escape) {
        this.text = text;
        this.escape = escape;
    }

    /**
     * <p>Constructor for HtmlTextNode.</p>
     *
     * @param text a {@link java.lang.String} object.
     */
    public HtmlTextNode(String text) {
        this(text, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderHtml() {
        return escape ? HtmlUtils.htmlEscape(text) : text;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlTextNode that = (HtmlTextNode) o;
        return Objects.equals(escape, that.escape) &&
                Objects.equals(text, that.text);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(text, escape);
    }
}
