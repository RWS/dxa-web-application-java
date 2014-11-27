package com.sdl.webapp.main.markup.html;

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
}
