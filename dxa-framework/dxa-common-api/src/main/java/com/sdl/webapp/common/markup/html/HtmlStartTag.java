package com.sdl.webapp.common.markup.html;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class HtmlStartTag extends HtmlRenderable {

    private final String tagName;

    private final List<HtmlAttribute> attributes;

    public HtmlStartTag(String tagName, List<HtmlAttribute> attributes) {
        this.tagName = tagName;
        this.attributes = ImmutableList.copyOf(attributes);
    }

    public String getTagName() {
        return tagName;
    }

    public List<HtmlAttribute> getAttributes() {
        return attributes;
    }

    @Override
    protected String renderHtml() {
        final StringBuilder sb = new StringBuilder().append('<').append(tagName);
        for (HtmlAttribute attribute : attributes) {
            sb.append(' ').append(attribute.toHtml());
        }
        return sb.append('>').toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HtmlStartTag that = (HtmlStartTag) o;

        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) return false;
        if (tagName != null ? !tagName.equals(that.tagName) : that.tagName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tagName != null ? tagName.hashCode() : 0;
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }
}
