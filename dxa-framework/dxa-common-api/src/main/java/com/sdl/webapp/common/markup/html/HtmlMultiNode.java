package com.sdl.webapp.common.markup.html;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @dxa.publicApi
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class HtmlMultiNode extends HtmlNode {

    private final HtmlNode[] nodes;

    public HtmlMultiNode(HtmlNode... nodes) {
        this.nodes = nodes;
    }

    @Override
    public String renderHtml() {
        final StringBuilder sb = new StringBuilder();
        for (HtmlNode node : nodes) {
            sb.append(node.toHtml());
        }
        return sb.toString();
    }
}
