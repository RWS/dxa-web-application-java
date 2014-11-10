package com.sdl.webapp.main.markup.html;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class HtmlElement extends HtmlNode {

    private final String tagName;

    private final boolean closeTag;

    private final List<HtmlAttribute> attributes;

    private final List<HtmlNode> content;

    public HtmlElement(String tagName, boolean closeTag, List<HtmlAttribute> attributes, List<HtmlNode> content) {
        this.tagName = tagName;
        this.closeTag = closeTag;
        this.attributes = ImmutableList.copyOf(attributes);
        this.content = ImmutableList.copyOf(content);
    }

    public String getTagName() {
        return tagName;
    }

    public List<HtmlAttribute> getAttributes() {
        return attributes;
    }

    public List<HtmlNode> getContent() {
        return content;
    }

    @Override
    protected String renderHtml() {
        final StringBuilder sb = new StringBuilder().append('<').append(tagName);
        for (HtmlAttribute attribute : attributes) {
            sb.append(' ').append(attribute.toHtml());
        }
        sb.append('>');
        for (HtmlNode node : content) {
            sb.append(node.toHtml());
        }
        if (closeTag) {
            sb.append("</").append(tagName).append('>');
        }
        return sb.toString();
    }
}
