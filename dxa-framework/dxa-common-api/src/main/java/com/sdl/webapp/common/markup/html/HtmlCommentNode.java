package com.sdl.webapp.common.markup.html;

import java.util.Objects;

public final class HtmlCommentNode extends HtmlNode {

    private final String text;

    public HtmlCommentNode(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String renderHtml() {
        return "<!-- " + text.replaceAll("<!--", "").replaceAll("-->", "") + " -->";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlCommentNode that = (HtmlCommentNode) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
