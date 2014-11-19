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

public class EntityTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(EntityTag.class);

    private String regionName;

    private String entityId;

    public void setRegion(String regionName) {
        this.regionName = regionName;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
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

        final Entity entity = region.getEntities().get(entityId);
        if (entity != null) {
            LOG.debug("Including entity: {}/{}", regionName, entityId);
            try {
                pageContext.include(ControllerUtils.getRequestPath(entity));
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing entity tag", e);
            }
        } else {
            LOG.debug("Entity not found in region: {}/{}", regionName, entityId);
        }

        return SKIP_BODY;
    }
}
