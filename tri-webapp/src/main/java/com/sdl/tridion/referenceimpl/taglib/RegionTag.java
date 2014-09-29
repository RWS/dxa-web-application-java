package com.sdl.tridion.referenceimpl.taglib;

import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.controller.core.ViewAttributeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class RegionTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(RegionTag.class);

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int doStartTag() throws JspException {
        final Page page = (Page) pageContext.getRequest().getAttribute(ViewAttributeNames.PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        if (page.getRegions().containsKey(name)) {
            LOG.debug("Including region: {}", name);
            try {
                pageContext.include("/region/" + name);
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing region tag", e);
            }
        } else {
            LOG.debug("Region not found: {}", name);
        }

        return SKIP_BODY;
    }
}
