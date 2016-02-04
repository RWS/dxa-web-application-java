package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.controller.ControllerUtils;
import com.sdl.webapp.common.markup.AbstractMarkupTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * <p>EntityTag class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class EntityTag extends AbstractMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(EntityTag.class);

    private EntityModel entity;

    private int containerSize;

    private String viewName;

    /**
     * <p>Setter for the field <code>containerSize</code>.</p>
     *
     * @param containerSize a int.
     */
    public void setContainerSize(int containerSize) {
        this.containerSize = containerSize;
    }

    /**
     * <p>Setter for the field <code>entity</code>.</p>
     *
     * @param entity a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     */
    public void setEntity(EntityModel entity) {
        this.entity = entity;
    }

    /**
     * <p>Setter for the field <code>viewName</code>.</p>
     *
     * @param viewName a {@link java.lang.String} object.
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        WebRequestContext webRequestContext = this.getWebRequestContext();

        if (!isNullOrEmpty(viewName)) {
            MvcDataCreator.creator(entity.getMvcData())
                    .mergeIn(
                            MvcDataCreator.creator().fromQualifiedName(viewName).create());
        }

        try {
            LOG.debug("Including entity into request: {}", entity.getId());
            pageContext.getRequest().setAttribute("_entity_", entity);

            webRequestContext.pushContainerSize(containerSize);
            this.decorateInclude(ControllerUtils.getIncludePath(entity), entity);
        } catch (ServletException | IOException e) {
            LOG.error("Error while processing entity tag", e);
            decorateException(entity);
        } finally {
            webRequestContext.popContainerSize();
        }

        return SKIP_BODY;
    }
}
