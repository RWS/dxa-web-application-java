package com.sdl.webapp.common.markup.html;

import org.springframework.web.util.HtmlUtils;

import java.util.Objects;

public final class HtmlTextNode extends HtmlNode {

    private final String text;

    private final boolean escape;

    public HtmlTextNode(String text, boolean escape) {
        this.text = text;
        this.escape = escape;
    }

    public HtmlTextNode(String text) {
        this(text, true);
    }

    @Override
    protected String renderHtml() {
        return escape ? HtmlUtils.htmlEscape(text) : text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlTextNode that = (HtmlTextNode) o;
        return Objects.equals(escape, that.escape) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, escape);
    }
}
