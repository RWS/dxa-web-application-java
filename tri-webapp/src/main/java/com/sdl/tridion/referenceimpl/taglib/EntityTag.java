package com.sdl.tridion.referenceimpl.taglib;

import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.controller.core.ViewAttributeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class EntityTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(EntityTag.class);

    @Override
    public int doStartTag() throws JspException {
        final Page page = (Page) pageContext.getRequest().getAttribute(ViewAttributeNames.PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        if (page.getEntities().containsKey(id)) {
            LOG.debug("Including entity: {}", id);
            try {
                pageContext.include("/entity/" + id);
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing entity tag", e);
            }
        } else {
            LOG.debug("Entity not found: {}", id);
        }

        return SKIP_BODY;
    }
}
