package com.sdl.webapp.common.markup.html;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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
        return StringUtils.isEmpty(tagName) ? "" : "</" + tagName + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlEndTag that = (HtmlEndTag) o;
        return Objects.equals(tagName, that.tagName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagName);
    }
}
