package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RichText;

@FunctionalInterface
public interface RichTextProcessor {
    /**
     * Resolves links in the specified content.
     *
     * @param xhtml The content which contains links to resolve
     * @return The post-processed content, with links resolved
     * @param localization a {@link com.sdl.webapp.common.api.localization.Localization} object.
     */
    RichText processRichText(String xhtml, Localization localization);
}
