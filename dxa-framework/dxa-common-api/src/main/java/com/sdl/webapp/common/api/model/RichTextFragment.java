package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;

/**
 * @dxa.publicApi
 */
@FunctionalInterface
public interface RichTextFragment {

    HtmlElement toHtmlElement() throws DxaException;
}
