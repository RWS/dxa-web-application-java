package com.sdl.webapp.common.markup.html;

import com.google.common.collect.ImmutableList;

import java.util.List;

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
    protected String renderHtml() {
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

        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (endTag != null ? !endTag.equals(that.endTag) : that.endTag != null) return false;
        if (startTag != null ? !startTag.equals(that.startTag) : that.startTag != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startTag != null ? startTag.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (endTag != null ? endTag.hashCode() : 0);
        return result;
    }
}
