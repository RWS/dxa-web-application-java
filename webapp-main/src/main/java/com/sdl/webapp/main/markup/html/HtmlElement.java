package com.sdl.webapp.main.markup.html;

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
}
