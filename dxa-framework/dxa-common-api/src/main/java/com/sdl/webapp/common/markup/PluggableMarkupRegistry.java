package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.markup.html.HtmlNode;

import java.util.List;

/**
 * <p>PluggableMarkupRegistry interface.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface PluggableMarkupRegistry {

    /**
     * <p>registerPluggableMarkup.</p>
     *
     * @param markupType a {@link com.sdl.webapp.common.markup.PluggableMarkupRegistry.MarkupType} object.
     * @param markup     a {@link com.sdl.webapp.common.markup.html.HtmlNode} object.
     */
    void registerPluggableMarkup(MarkupType markupType, HtmlNode markup);

    /**
     * <p>registerPluggableMarkup.</p>
     *
     * @param label  a {@link java.lang.String} object.
     * @param markup a {@link com.sdl.webapp.common.markup.html.HtmlNode} object.
     */
    void registerPluggableMarkup(String label, HtmlNode markup);

    /**
     * <p>registerContextualPluggableMarkup.</p>
     *
     * @param markupType a {@link com.sdl.webapp.common.markup.PluggableMarkupRegistry.MarkupType} object.
     * @param markup a {@link com.sdl.webapp.common.markup.html.HtmlNode} object.
     */
    void registerContextualPluggableMarkup(MarkupType markupType, HtmlNode markup);

    /**
     * <p>registerContextualPluggableMarkup.</p>
     *
     * @param label a {@link java.lang.String} object.
     * @param markup a {@link com.sdl.webapp.common.markup.html.HtmlNode} object.
     */
    void registerContextualPluggableMarkup(String label, HtmlNode markup);

    /**
     * <p>getPluggableMarkup.</p>
     *
     * @param label a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<HtmlNode> getPluggableMarkup(String label);

    enum MarkupType {
        CSS,
        TOP_JS,
        BOTTOM_JS
    }
}
