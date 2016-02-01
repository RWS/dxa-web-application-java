package com.sdl.webapp.common.markup.html;

import java.util.Arrays;
import java.util.Objects;

/**
 * <p>HtmlMultiNode class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
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
     * <p>Getter for the field <code>nodes</code>.</p>
     *
     * @return an array of {@link com.sdl.webapp.common.markup.html.HtmlNode} objects.
     */
    public HtmlNode[] getNodes() {
        return nodes;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlMultiNode that = (HtmlMultiNode) o;
        return Arrays.equals(nodes, that.nodes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(nodes);
    }
}
