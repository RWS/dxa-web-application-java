package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.markup.html.HtmlNode;

import java.util.List;

/**
 * PluggableMarkupRegistry
 *
 * @author nic
 */
public interface PluggableMarkupRegistry {

    public enum MarkupType {

        CSS,
        TOP_JS,
        BOTTOM_JS
    };

    public void registerPluggableMarkup(MarkupType markupType, HtmlNode markup);

    public void registerPluggableMarkup(String label, HtmlNode markup);

    public void registerContextualPluggableMarkup(MarkupType markupType, HtmlNode markup);

    public void registerContextualPluggableMarkup(String label, HtmlNode markup);

    public List<HtmlNode> getPluggableMarkup(String label);

}
