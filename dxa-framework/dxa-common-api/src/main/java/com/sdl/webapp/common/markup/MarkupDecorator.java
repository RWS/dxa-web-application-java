package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.markup.html.HtmlNode;

/**
 * MarkupDecorator
 *
 * @author nic
 */
public interface MarkupDecorator {

    public HtmlNode process(HtmlNode markup, ViewModel model, WebRequestContext webRequestContext);

    public int getPriority();
}
