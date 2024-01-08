package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;

@FunctionalInterface
public interface RichTextFragment {

    HtmlElement toHtmlElement() throws DxaException;
}
