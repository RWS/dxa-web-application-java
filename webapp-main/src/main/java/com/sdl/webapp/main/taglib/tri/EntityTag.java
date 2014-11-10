package com.sdl.webapp.main.taglib.tri;

import com.sdl.webapp.common.api.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import static com.sdl.webapp.main.RequestAttributeNames.PAGE_MODEL;
import static com.sdl.webapp.main.controller.core.AbstractController.ENTITY_PATH_PREFIX;

public class EntityTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(EntityTag.class);

    private String regionName;

    private int index;

    public void setRegion(String regionName) {
        this.regionName = regionName;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int doStartTag() throws JspException {
        final Page page = (Page) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        LOG.debug("Including entity: {}/{}", regionName, index);
        try {
            pageContext.include(String.format("%s/%s/%d", ENTITY_PATH_PREFIX, regionName, index));
        } catch (ServletException | IOException e) {
            throw new JspException("Error while processing entity tag", e);
        }

        return SKIP_BODY;
    }
}
