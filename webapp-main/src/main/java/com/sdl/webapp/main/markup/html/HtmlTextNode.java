package com.sdl.webapp.main.markup.html;

import org.springframework.web.util.HtmlUtils;

public final class HtmlTextNode extends HtmlNode {

    private final String text;

    public HtmlTextNode(String text) {
        this.text = text;
    }

    @Override
    protected String renderHtml() {
        return HtmlUtils.htmlEscape(text);
    }
}
