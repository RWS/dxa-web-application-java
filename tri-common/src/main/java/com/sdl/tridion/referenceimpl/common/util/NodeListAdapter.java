package com.sdl.tridion.referenceimpl.common.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.AbstractList;

/**
 * Adapter to make the horrible {@code org.w3c.dom.NodeList} API more useable.
 */
public class NodeListAdapter extends AbstractList<Node> {

    private final NodeList nodeList;

    public NodeListAdapter(NodeList nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public Node get(int index) {
        return nodeList.item(index);
    }

    @Override
    public int size() {
        return nodeList.getLength();
    }
}
