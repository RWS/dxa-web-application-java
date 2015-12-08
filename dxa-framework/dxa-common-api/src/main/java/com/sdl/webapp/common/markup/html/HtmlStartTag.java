package com.sdl.webapp.common.markup.html;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

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
        if (StringUtils.isEmpty(tagName)) {
            return "";
        }

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
        return Objects.equals(tagName, that.tagName) &&
                Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagName, attributes);
    }
}
