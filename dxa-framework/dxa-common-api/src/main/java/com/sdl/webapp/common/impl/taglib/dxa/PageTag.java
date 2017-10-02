package com.sdl.webapp.common.impl.taglib.dxa;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.controller.ControllerUtils;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;

@Setter
public class PageTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(PageTag.class);

    private String name;

    private String viewName;

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        final PageModel page = (PageModel) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        final RegionModel includePage = page.getRegions().get(name);
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
