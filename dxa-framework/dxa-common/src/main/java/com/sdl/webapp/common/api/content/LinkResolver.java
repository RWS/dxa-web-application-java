package com.sdl.webapp.common.api.content;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Link resolver that resolves links to components.
 *
 * @dxa.publicApi
 */
@FunctionalInterface
public interface LinkResolver {

    /**
     * Resolves a link. This translates the input URL to a link that can be used on a web page. What the input URL
     * is exactly depends on the implementation and what the source of the data is - it might for example be a Tridion
     * "tcm:" URL which refers to a Tridion component.
     *
     * @param url             The TCM URI to resolve.
     * @param localizationId  The localization ID to use.
     * @param resolveToBinary whether the expected URL is an URL to a binary
     * @return The translated URL.
     */
    @Contract("null, _, _ -> null; !null, _, _ -> !null")
    default String resolveLink(@Nullable String url, @Nullable String localizationId, boolean resolveToBinary){
        return resolveLink(url, localizationId, resolveToBinary, null);
    }
    /**
     * Resolves a link. This translates the input URL to a link that can be used on a web page. What the input URL
     * is exactly depends on the implementation and what the source of the data is - it might for example be a Tridion
     * "tcm:" URL which refers to a Tridion component.
     *
     * @param url            The TCM URI to resolve.
     * @param localizationId The localization ID to use.
     * @return The translated URL.
     */
    @Contract("null, _ -> null; !null, _ -> !null")
    default String resolveLink(@Nullable String url, @Nullable String localizationId) {
        return resolveLink(url, localizationId, false, null);
    }

    /**
     * Resolves a link. This translates the input URL to a link that can be used on a web page. What the input URL
     * is exactly depends on the implementation and what the source of the data is - it might for example be a Tridion
     * "tcm:" URL which refers to a Tridion component.
     *
     * @param url            The TCM URI to resolve.
     * @param localizationId The localization ID to use.
     * @param contextId The ID of the context page within which we are resolving
     * @return The translated URL.
     */
    @Contract("null, _, _ -> null; !null, _, _ -> !null")
    default String resolveLink(@Nullable String url, @Nullable String localizationId, @Nullable String contextId) {
        return resolveLink(url, localizationId, false, contextId);
    }

    /**
     * Resolves a link. This translates the input URL to a link that can be used on a web page. What the input URL
     * is exactly depends on the implementation and what the source of the data is - it might for example be a Tridion
     * "tcm:" URL which refers to a Tridion component.
     *
     * @param url             The TCM URI to resolve.
     * @param localizationId  The localization ID to use.
     * @param resolveToBinary whether the expected URL is an URL to a binary
     * @param contextId The ID of the context page within which we are resolving
     * @return The translated URL.
     */
    @Contract("null, _, _, _ -> null; !null, _, _, _ -> !null")
    String resolveLink(@Nullable String url, @Nullable String localizationId, boolean resolveToBinary, @Nullable String contextId);

}
