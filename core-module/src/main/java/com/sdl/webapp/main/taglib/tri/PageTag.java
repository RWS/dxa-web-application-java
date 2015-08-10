package com.sdl.webapp.main.taglib.tri;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.controller.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;

public class PageTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(PageTag.class);

    private String name;

    private String viewName;

    public void setName(String name) {
        this.name = name;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public int doStartTag() throws JspException {
        final Page page = (Page) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        final Page includePage = page.getIncludes().get(name);
        if (includePage != null) {
            // Use alternate view name if specified
            if (!Strings.isNullOrEmpty(viewName)) {
                includePage.getMvcData().getRouteValues().put("viewName", viewName);
            }

            try {
                pageContext.include(ControllerUtils.getIncludePath(includePage));
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing include tag: " + name, e);
            }
        } else {
            LOG.debug("Include page not found on page: {}", name);
        }

        return SKIP_BODY;
    }
}
