package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.springframework.core.Ordered;

/**
 * @dxa.publicApi
 */
public interface MarkupDecorator extends Ordered {

    HtmlNode process(HtmlNode markup, ViewModel model, WebRequestContext webRequestContext);
}
