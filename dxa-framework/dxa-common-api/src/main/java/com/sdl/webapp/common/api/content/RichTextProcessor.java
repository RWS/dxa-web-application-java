package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RichText;

public interface RichTextProcessor {
    /**
     * Resolves links in the specified content.
     *
     * @param xhtml The content which contains links to resolve
     * @return The post-processed content, with links resolved
     */
    RichText processRichText(String xhtml, Localization localization);
}
