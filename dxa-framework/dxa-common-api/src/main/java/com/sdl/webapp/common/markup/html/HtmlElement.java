package com.sdl.webapp.common.markup.html;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * @dxa.publicApi
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public final class HtmlElement extends HtmlNode {

    private final HtmlStartTag startTag;

    private final List<HtmlNode> content;

    private final HtmlEndTag endTag;

    public HtmlElement(String tagName, boolean closeTag, List<HtmlAttribute> attributes, List<HtmlNode> content) {
        this.startTag = new HtmlStartTag(tagName, attributes);
        this.content = ImmutableList.copyOf(content);
        this.endTag = closeTag ? new HtmlEndTag(tagName) : null;
    }

    @Override
    public String renderHtml() {
        final StringBuilder sb = new StringBuilder(1024).append(startTag.toHtml());
        for (HtmlNode node : content) {
            sb.append(node.toHtml());
        }
        if (endTag != null) {
            sb.append(endTag.toHtml());
        }
        return sb.toString();
    }
}
