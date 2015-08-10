package com.sdl.webapp.common.api.content;

/**
 * Content resolver. Resolves links to components and links in content.
 */
public interface ContentResolver {

    /**
     * Resolves a link. This translates the input URL to a link that can be used on a web page. What the input URL
     * is exactly depends on the implementation and what the source of the data is - it might for example be a Tridion
     * "tcm:" URL which refers to a Tridion component.
     *
     * @param url The URL to resolve.
     * @param localizationId The localization to use.
     * @return The translated URL.
     */
    String resolveLink(String url, String localizationId);

    /**
     * Resolves links in the specified content.
     *
     * @param content The content which contains links to resolve.
     * @return The content, with links resolved.
     */
    String resolveContent(String content);
}
