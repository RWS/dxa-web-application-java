package com.sdl.webapp.main.taglib;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import static com.sdl.webapp.main.RequestAttributeNames.PAGE_MODEL;
import static com.sdl.webapp.main.controller.core.AbstractController.PAGE_PATH_PREFIX;

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

        if (page.getIncludes().containsKey(name)) {
            LOG.debug("Including page: {}", name);
            try {
                final StringBuilder sb = new StringBuilder();
                sb.append(PAGE_PATH_PREFIX).append('/').append(name);
                if (!Strings.isNullOrEmpty(viewName)) {
                    sb.append("?viewName=").append(viewName);
                }

                pageContext.include(sb.toString());
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing include tag: " + name, e);
            } finally {
                // Restore request attribute to original page model
                pageContext.getRequest().setAttribute(PAGE_MODEL, page);
            }
        } else {
            LOG.debug("Include page not found: {}", name);
        }

        return SKIP_BODY;
    }
}
