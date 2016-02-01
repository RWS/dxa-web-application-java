package com.sdl.webapp.common.markup.html;

import java.util.Objects;

public class HtmlMultiNode extends HtmlNode {

    private final HtmlNode[] nodes;

    public HtmlMultiNode(HtmlNode... nodes) {
        this.nodes = nodes;
    }

    public HtmlNode[] getNodes() {
        return nodes;
    }

    @Override
    public String renderHtml() {
        final StringBuilder sb = new StringBuilder();
        for (HtmlNode node : nodes) {
            sb.append(node.toHtml());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlMultiNode that = (HtmlMultiNode) o;
        return Objects.equals(nodes, that.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes);
    }
}
