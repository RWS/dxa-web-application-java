package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.markup.html.HtmlNode;

/**
 * <p>MarkupDecorator interface.
 * todo dxa2: extend {@link org.springframework.core.Ordered}</p>
 */
public interface MarkupDecorator {

    /**
     * <p>process.</p>
     *
     * @param markup            a {@link com.sdl.webapp.common.markup.html.HtmlNode} object.
     * @param model             a {@link com.sdl.webapp.common.api.model.ViewModel} object.
     * @param webRequestContext a {@link com.sdl.webapp.common.api.WebRequestContext} object.
     * @return a {@link com.sdl.webapp.common.markup.html.HtmlNode} object.
     */
    HtmlNode process(HtmlNode markup, ViewModel model, WebRequestContext webRequestContext);

    /**
     * <p>getPriority.</p>
     *
     * @deprecated todo dxa2 remove
     * @return a int.
     */
    @Deprecated
    int getPriority();
}
