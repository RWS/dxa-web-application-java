package com.sdl.webapp.common.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.AbstractList;

/**
 * Adapter to make dealing with {@code org.w3c.dom.NamedNodeMap} easier.
 */
public class NamedNodeMapAdapter extends AbstractList<Node> {

    private final NamedNodeMap namedNodeMap;

    public NamedNodeMapAdapter(NamedNodeMap namedNodeMap) {
        this.namedNodeMap = namedNodeMap;
    }

    @Override
    public Node get(int index) {
        return namedNodeMap.item(index);
    }

    @Override
    public int size() {
        return namedNodeMap.getLength();
    }
}
