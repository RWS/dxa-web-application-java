package com.sdl.webapp.common.markup.html;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HtmlEndTag that = (HtmlEndTag) o;

        if (tagName != null ? !tagName.equals(that.tagName) : that.tagName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tagName != null ? tagName.hashCode() : 0;
    }
}
