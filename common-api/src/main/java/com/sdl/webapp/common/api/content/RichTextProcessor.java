package com.sdl.webapp.common.api.content;

public interface RichTextProcessor {
	 /**
     * Resolves links in the specified content.
     *
     * @param content The content which contains links to resolve.
     * @return The content, with links resolved.
     */
    String resolveContent(String content);
}
