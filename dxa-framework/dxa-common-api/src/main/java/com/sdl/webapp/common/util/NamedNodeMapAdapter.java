package com.sdl.webapp.common.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.AbstractList;

/**
 * Adapter to make dealing with {@code org.w3c.dom.NamedNodeMap} easier.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class NamedNodeMapAdapter extends AbstractList<Node> {

    private final NamedNodeMap namedNodeMap;

    /**
     * <p>Constructor for NamedNodeMapAdapter.</p>
     *
     * @param namedNodeMap a {@link org.w3c.dom.NamedNodeMap} object.
     */
    public NamedNodeMapAdapter(NamedNodeMap namedNodeMap) {
        this.namedNodeMap = namedNodeMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node get(int index) {
        return namedNodeMap.item(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return namedNodeMap.getLength();
    }
}
