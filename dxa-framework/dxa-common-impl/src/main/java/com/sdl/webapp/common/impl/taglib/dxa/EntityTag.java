package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.controller.ControllerUtils;
import com.sdl.webapp.common.markup.AbstractMarkupTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import java.io.IOException;

public class EntityTag extends AbstractMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(EntityTag.class);

    private EntityModel entity;

    private int containerSize;

    public void setContainerSize(int containerSize) {
        this.containerSize = containerSize;
    }

    public void setEntity(EntityModel entity) {
        this.entity = entity;
    }

    @Override
    public int doStartTag() throws JspException {
        WebRequestContext webRequestContext = this.getWebRequestContext();

        try {
            LOG.debug("Including entity into request: {}", entity.getId());
            pageContext.getRequest().setAttribute("_entity_", entity);

            webRequestContext.pushContainerSize(containerSize);
            this.decorateInclude(ControllerUtils.getIncludePath(entity), entity);
        } catch (ServletException | IOException e) {
            throw new JspException("Error while processing entity tag", e);
        } finally {
            webRequestContext.popContainerSize();
        }

        return SKIP_BODY;
    }
}
