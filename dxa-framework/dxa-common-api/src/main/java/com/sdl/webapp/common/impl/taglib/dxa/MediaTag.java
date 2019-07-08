package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.api.MediaHelper;
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

    private Double aspect;

    private String cssClass;

    private int containerSize;

    private MediaHelper mediaHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    protected HtmlElement generateElement() throws DxaException {
        if (media == null) {
            return null;
        }

        if (aspect == null) {
            return media.toHtmlElement(this.widthFactor, getMediaHelper().getDefaultMediaAspect(),
                    this.cssClass, this.containerSize, this.getContextPath());
        }

        return media.toHtmlElement(this.widthFactor, this.aspect, this.cssClass, this.containerSize, this.getContextPath());
    }

    private MediaHelper getMediaHelper() {
        if (mediaHelper == null) {
            mediaHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                    .getBean(MediaHelper.class);
        }
        return mediaHelper;
    }

    private String getContextPath() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getContextPath();
    }
}
