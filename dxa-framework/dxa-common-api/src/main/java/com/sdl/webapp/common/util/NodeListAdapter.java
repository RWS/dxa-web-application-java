package com.sdl.webapp.common.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.AbstractList;

/**
 * Adapter to make dealing with {@code org.w3c.dom.NodeList} easier.
 */
public final class NodeListAdapter extends AbstractList<Node> {

    private final NodeList nodeList;

    /**
     * <p>Constructor for NodeListAdapter.</p>
     *
     * @param nodeList a {@link org.w3c.dom.NodeList} object.
     */
    public NodeListAdapter(NodeList nodeList) {
        this.nodeList = nodeList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node get(int index) {
        return nodeList.item(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return nodeList.getLength();
    }
}
