package com.sdl.webapp.common.markup.html;

import org.springframework.web.util.HtmlUtils;

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

        if (escape != that.escape) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (escape ? 1 : 0);
        return result;
    }
}
