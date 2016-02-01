package com.sdl.webapp.common.markup.html;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

public final class HtmlElement extends HtmlNode {

    private final HtmlStartTag startTag;

    private final List<HtmlNode> content;

    private final HtmlEndTag endTag;

    public HtmlElement(String tagName, boolean closeTag, List<HtmlAttribute> attributes, List<HtmlNode> content) {
        this.startTag = new HtmlStartTag(tagName, attributes);
        this.content = ImmutableList.copyOf(content);
        this.endTag = closeTag ? new HtmlEndTag(tagName) : null;
    }

    public HtmlStartTag getStartTag() {
        return startTag;
    }

    public HtmlEndTag getEndTag() {
        return endTag;
    }

    public List<HtmlNode> getContent() {
        return content;
    }

    @Override
    public String renderHtml() {
        final StringBuilder sb = new StringBuilder().append(startTag.toHtml());
        for (HtmlNode node : content) {
            sb.append(node.toHtml());
        }
        if (endTag != null) {
            sb.append(endTag.toHtml());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlElement that = (HtmlElement) o;
        return Objects.equals(startTag, that.startTag) &&
                Objects.equals(content, that.content) &&
                Objects.equals(endTag, that.endTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTag, content, endTag);
    }
}
