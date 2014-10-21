package com.sdl.webapp.main.taglib;

import com.sdl.webapp.common.api.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import static com.sdl.webapp.main.WebAppConstants.PAGE_PATH_PREFIX;
import static com.sdl.webapp.main.WebAppConstants.PAGE_MODEL;

public class IncludeTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(IncludeTag.class);

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

        if (page.getIncludes().containsKey(name)) {
            LOG.debug("Including page: {}", name);
            try {
                pageContext.include(PAGE_PATH_PREFIX + "/" + name);
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
