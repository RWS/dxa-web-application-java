package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.impl.interceptor.csrf.CsrfUtils;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;

public class CsrfTokenTag extends HtmlElementTag {

    @Override
    protected HtmlElement generateElement() throws DxaException {
        return HtmlBuilders.element("input")
                .withAttribute("name", CsrfUtils.CSRF_TOKEN_NAME)
                .withAttribute("value", CsrfUtils.setToken(pageContext.getSession()))
                .withAttribute("type", "hidden")
                .build();
    }
}
