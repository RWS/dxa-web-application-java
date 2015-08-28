package com.sdl.webapp.common.markup.html;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HtmlCommentNode that = (HtmlCommentNode) o;

        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }
}
