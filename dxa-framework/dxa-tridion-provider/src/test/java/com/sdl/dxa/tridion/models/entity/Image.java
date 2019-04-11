package com.sdl.dxa.tridion.models.entity;

import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;

public class Image extends MediaItem {

    public String alternateText;

    @Override
    public HtmlElement toHtmlElement(String widthFactor) throws DxaException {
        return null;
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize) throws DxaException {
        return null;
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) throws DxaException {
        return null;
    }
}
