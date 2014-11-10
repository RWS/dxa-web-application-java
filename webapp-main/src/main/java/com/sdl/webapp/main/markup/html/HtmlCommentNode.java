package com.sdl.webapp.main.markup.html;

public final class HtmlCommentNode extends HtmlNode {

    private final String text;

    public HtmlCommentNode(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    protected String renderHtml() {
        return "<!-- " + text.replaceAll("<!--", "").replaceAll("-->", "") + " -->";
    }
}
