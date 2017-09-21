package com.sdl.webapp.common.api.mapping.semantic.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementors of this interface can provide XPath with their current state.
 */
@FunctionalInterface
public interface WithXPath {

    /**
     * Generates XPath for this semantic field respecting the context XPath and type of the field (metadata or content).
     *
     * @param contextXPath the current context XPath, optional, may be {@code null}
     * @return generated XPath
     */
    @NotNull
    String getXPath(@Nullable String contextXPath);
}
