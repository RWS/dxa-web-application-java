package com.sdl.webapp.common.api.content;

/**
 * Content resolver.
 */
public interface ContentResolver {

    String resolveLink(String url, String localizationId);

    String resolveContent(String content);
}
