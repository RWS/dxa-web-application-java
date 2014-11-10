package com.sdl.webapp.main.markup.html;

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
}
