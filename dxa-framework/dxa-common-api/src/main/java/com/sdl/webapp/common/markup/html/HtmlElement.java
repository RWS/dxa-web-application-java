package com.sdl.webapp.common.markup.html;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class HtmlElement extends HtmlNode {

    private final HtmlStartTag startTag;

    private final List<HtmlNode> content;

    private final HtmlEndTag endTag;

    /**
     * <p>Constructor for HtmlElement.</p>
     *
     * @param tagName    a {@link java.lang.String} object.
     * @param closeTag   a boolean.
     * @param attributes a {@link java.util.List} object.
     * @param content    a {@link java.util.List} object.
     */
    public HtmlElement(String tagName, boolean closeTag, List<HtmlAttribute> attributes, List<HtmlNode> content) {
        this.startTag = new HtmlStartTag(tagName, attributes);
        this.content = ImmutableList.copyOf(content);
        this.endTag = closeTag ? new HtmlEndTag(tagName) : null;
    }

    /**
     * {@inheritDoc}
     */
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
