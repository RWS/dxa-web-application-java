package com.sdl.webapp.main.markup.html;

public final class HtmlEndTag extends HtmlRenderable {

    private final String tagName;

    public HtmlEndTag(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    @Override
    protected String renderHtml() {
        return "</" + tagName + ">";
    }
}
