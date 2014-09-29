package com.sdl.tridion.referenceimpl.taglib;

import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.Region;
import com.sdl.tridion.referenceimpl.controller.core.ViewAttributeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class EntitiesTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(EntitiesTag.class);

    private String region;

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public int doStartTag() throws JspException {
        final Page page = (Page) pageContext.getRequest().getAttribute(ViewAttributeNames.PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        final Region reg = page.getRegions().get(region);
        if (reg == null) {
            LOG.debug("Region not found: {}", region);
            return SKIP_BODY;
        }

        for (Entity entity : reg.getEntities()) {
            final String entityId = entity.getId();
            LOG.debug("Including entity: {}", entityId);
            try {
                pageContext.include("/entity/" + entityId);
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing entities tag", e);
            }
        }

        return SKIP_BODY;
    }
}
