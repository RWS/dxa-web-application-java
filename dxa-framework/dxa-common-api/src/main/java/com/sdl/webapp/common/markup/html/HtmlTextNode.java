package com.sdl.webapp.common.markup.html;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.web.util.HtmlUtils;

import java.util.Objects;

@EqualsAndHashCode(callSuper = false)
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

}
