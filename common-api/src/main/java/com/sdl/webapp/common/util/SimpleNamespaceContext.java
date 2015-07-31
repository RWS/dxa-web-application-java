package com.sdl.webapp.common.util;

import com.google.common.collect.ImmutableBiMap;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

/**
 * Implementation of {@code javax.xml.namespace.NamespaceContext} that gets information from a fixed map containing
 * prefixes and namespace URIs.
 */
public final class SimpleNamespaceContext implements NamespaceContext {

    private final ImmutableBiMap<String, String> namespacesByPrefix;

    public SimpleNamespaceContext(ImmutableBiMap<String, String> namespacesByPrefix) {
        this.namespacesByPrefix = namespacesByPrefix;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return namespacesByPrefix.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return namespacesByPrefix.inverse().get(namespaceURI);
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        return namespacesByPrefix.keySet().iterator();
    }
}
