package com.sdl.webapp.common.api.content;

/**
 * Link resolver. Resolves links to components.
 */
public interface LinkResolver {

    /**
     * Resolves a link. This translates the input URL to a link that can be used on a web page. What the input URL
     * is exactly depends on the implementation and what the source of the data is - it might for example be a Tridion
     * "tcm:" URL which refers to a Tridion component.
     *
     * @param url            The URL to resolve.
     * @param localizationId The localization to use.
     * @return The translated URL.
     * @param resolveToBinary a boolean.
     */
    String resolveLink(String url, String localizationId, boolean resolveToBinary);

    /**
     * <p>resolveLink.</p>
     *
     * @param url            a {@link java.lang.String} object.
     * @param localizationId a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    String resolveLink(String url, String localizationId);
}
