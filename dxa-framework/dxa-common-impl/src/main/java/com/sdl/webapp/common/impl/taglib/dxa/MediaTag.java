package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>MediaTag class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class MediaTag extends HtmlElementTag {
    private static final Logger LOG = LoggerFactory.getLogger(MediaTag.class);

    private MediaItem media;
    private String widthFactor;
    private double aspect;
    private String cssClass;
    private int containerSize;

    /**
     * <p>Setter for the field <code>media</code>.</p>
     *
     * @param media a {@link com.sdl.webapp.common.api.model.entity.MediaItem} object.
     */
    public void setMedia(MediaItem media) {
        this.media = media;
    }

    /**
     * <p>Setter for the field <code>widthFactor</code>.</p>
     *
     * @param widthFactor a {@link java.lang.String} object.
     */
    public void setWidthFactor(String widthFactor) {
        this.widthFactor = widthFactor;
    }

    /**
     * <p>Setter for the field <code>aspect</code>.</p>
     *
     * @param aspect a double.
     */
    public void setAspect(double aspect) {
        this.aspect = aspect;
    }

    /**
     * <p>Setter for the field <code>cssClass</code>.</p>
     *
     * @param cssClass a {@link java.lang.String} object.
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * <p>Setter for the field <code>containerSize</code>.</p>
     *
     * @param containerSize a int.
     */
    public void setContainerSize(int containerSize) {
        this.containerSize = containerSize;
    }

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
