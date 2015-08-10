package com.sdl.webapp.common.markup.html;

import java.util.Arrays;

public class HtmlMultiNode extends HtmlNode {

    private final HtmlNode[] nodes;

    public HtmlMultiNode(HtmlNode... nodes) {
        this.nodes = nodes;
    }

    public HtmlNode[] getNodes() {
        return nodes;
    }

    @Override
    protected String renderHtml() {
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

        if (!Arrays.equals(nodes, that.nodes)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return nodes != null ? Arrays.hashCode(nodes) : 0;
    }
}
