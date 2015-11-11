package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class MediaTag extends HtmlElementTag {
    private static final Logger LOG = LoggerFactory.getLogger(MediaTag.class);

    private MediaItem media;
    private String widthFactor;
    private double aspect;
    private String cssClass;
    private int containerSize;

    public void setMedia(MediaItem media) {
        this.media = media;
    }

    public void setWidthFactor(String widthFactor) {
        this.widthFactor = widthFactor;
    }

    public void setAspect(double aspect) {
        this.aspect = aspect;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setContainerSize(int containerSize) {
        this.containerSize = containerSize;
    }

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
