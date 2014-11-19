package com.sdl.webapp.main.taglib.tri;

import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.main.controller.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import static com.sdl.webapp.main.RequestAttributeNames.PAGE_MODEL;

public class RegionTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(RegionTag.class);

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int doStartTag() throws JspException {
        final Page page = (Page) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        final Region region = page.getRegions().get(name);
        if (region != null) {
            LOG.debug("Including region: {}", name);
            try {
                pageContext.include(ControllerUtils.getIncludePath(region));
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing region tag: " + name, e);
            }
        } else {
            LOG.debug("Region not found on page: {}", name);
        }

        return SKIP_BODY;
    }
}
