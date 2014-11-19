package com.sdl.webapp.main.taglib.tri;

import com.sdl.webapp.common.api.model.Entity;
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

public class EntitiesTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(EntitiesTag.class);

    private String regionName;

    public void setRegion(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public int doStartTag() throws JspException {
        final Page page = (Page) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        final Region region = page.getRegions().get(regionName);
        if (region == null) {
            LOG.debug("Region not found on page: {}", regionName);
            return SKIP_BODY;
        }

        for (Entity entity : region.getEntities().values()) {
            try {
                pageContext.include(ControllerUtils.getRequestPath(entity));
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing entity tag", e);
            }
        }

        return SKIP_BODY;
    }
}
