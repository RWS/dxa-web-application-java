package com.sdl.webapp.common.markup.html;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class HtmlMultiNode extends HtmlNode {

    private final HtmlNode[] nodes;

    /**
     * <p>Constructor for HtmlMultiNode.</p>
     *
     * @param nodes a {@link com.sdl.webapp.common.markup.html.HtmlNode} object.
     */
    public HtmlMultiNode(HtmlNode... nodes) {
        this.nodes = nodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderHtml() {
        final StringBuilder sb = new StringBuilder();
        for (HtmlNode node : nodes) {
            sb.append(node.toHtml());
        }
        return sb.toString();
    }
}
