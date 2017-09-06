package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import lombok.Setter;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Setter
public class MediaTag extends HtmlElementTag {

    private MediaItem media;

    private String widthFactor;

    private double aspect;

    private String cssClass;

    private int containerSize;

    /**
     * {@inheritDoc}
     */
    @Override
    protected HtmlElement generateElement() throws DxaException {
        if (media == null) {
            return null;
        }

        return media.toHtmlElement(this.widthFactor, this.aspect, this.cssClass, this.containerSize, this.getContextPath());
    }

    private String getContextPath() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getContextPath();
    }
}
