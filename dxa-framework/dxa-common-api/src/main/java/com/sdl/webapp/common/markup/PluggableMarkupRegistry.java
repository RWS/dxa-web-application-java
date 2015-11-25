package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.markup.html.HtmlNode;

import java.util.List;

public interface PluggableMarkupRegistry {

    void registerPluggableMarkup(MarkupType markupType, HtmlNode markup);

    void registerPluggableMarkup(String label, HtmlNode markup);

    void registerContextualPluggableMarkup(MarkupType markupType, HtmlNode markup);

    void registerContextualPluggableMarkup(String label, HtmlNode markup);

    List<HtmlNode> getPluggableMarkup(String label);

    enum MarkupType {
        CSS,
        TOP_JS,
        BOTTOM_JS
    }
}
