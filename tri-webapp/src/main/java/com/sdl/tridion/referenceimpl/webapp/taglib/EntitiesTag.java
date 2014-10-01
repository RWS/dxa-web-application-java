package com.sdl.tridion.referenceimpl.webapp.taglib;

import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.Region;
import com.sdl.tridion.referenceimpl.webapp.controller.core.ViewAttributeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class EntitiesTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(EntitiesTag.class);

    private String regionName;

    public void setRegion(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public int doStartTag() throws JspException {
        final Page page = (Page) pageContext.getRequest().getAttribute(ViewAttributeNames.PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        final Region region = page.getRegions().get(regionName);
        if (region == null) {
            LOG.debug("Region not found: {}", regionName);
            return SKIP_BODY;
        }

        int count = region.getEntities().size();
        for (int index = 0; index < count; index++) {
            try {
                pageContext.include(String.format("/entity/%s/%d", regionName, index));
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing entity tag", e);
            }
        }

        return SKIP_BODY;
    }
}
