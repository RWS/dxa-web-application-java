package com.sdl.tridion.referenceimpl.webapp.taglib;

import com.sdl.tridion.referenceimpl.common.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import static com.sdl.tridion.referenceimpl.webapp.WebAppConstants.ENTITY_PATH_PREFIX;
import static com.sdl.tridion.referenceimpl.webapp.WebAppConstants.PAGE_MODEL;

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

        try {
            pageContext.include(String.format("%s%s/%d", ENTITY_PATH_PREFIX, regionName, index));
        } catch (ServletException | IOException e) {
            throw new JspException("Error while processing entity tag", e);
        }

        return SKIP_BODY;
    }
}
