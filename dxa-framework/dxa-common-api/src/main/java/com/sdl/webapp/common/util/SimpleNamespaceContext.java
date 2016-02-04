package com.sdl.webapp.common.util;

import com.google.common.collect.ImmutableBiMap;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

/**
 * Implementation of {@code javax.xml.namespace.NamespaceContext} that gets information from a fixed map containing
 * prefixes and namespace URIs.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class SimpleNamespaceContext implements NamespaceContext {

    private final ImmutableBiMap<String, String> namespacesByPrefix;

    /**
     * <p>Constructor for SimpleNamespaceContext.</p>
     *
     * @param namespacesByPrefix a {@link com.google.common.collect.ImmutableBiMap} object.
     */
    public SimpleNamespaceContext(ImmutableBiMap<String, String> namespacesByPrefix) {
        this.namespacesByPrefix = namespacesByPrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNamespaceURI(String prefix) {
        return namespacesByPrefix.get(prefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPrefix(String namespaceURI) {
        return namespacesByPrefix.inverse().get(namespaceURI);
    }

    /** {@inheritDoc} */
    @Override
    public Iterator getPrefixes(String namespaceURI) {
        return namespacesByPrefix.keySet().iterator();
    }
}
