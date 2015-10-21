package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.markup.AbstractMarkupTag;
import com.sdl.webapp.common.controller.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import java.io.IOException;

import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;

public class EntityTag extends AbstractMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(EntityTag.class);

    private String regionName;

    private String entityId;

    private RegionModel parentRegion;
    
    private EntityModel entityRef;
    
    private int containerSize;
    
    public void setRegion(String regionName) {
        this.regionName = regionName;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setContainerSize(int containerSize) {
        this.containerSize = containerSize;
    }
    
    public void setEntity(EntityModel entity) { this.entityRef = entity; }

    @Override
    public int doStartTag() throws JspException {
        final PageModel page = (PageModel) pageContext.getRequest().getAttribute(PAGE_MODEL);

        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        RegionModel region = null;
        final EntityModel entity;
        WebRequestContext webRequestContext = this.getWebRequestContext();
        
        if (entityId != null) {

            parentRegion = webRequestContext.getParentRegion();

            if (parentRegion != null) {
                region = parentRegion;
            } else {
                region = page.getRegions().get(regionName);
            }
            if (region == null) {
                LOG.debug("Region not found on page: {}", regionName);
                return SKIP_BODY;
            }

            entity = region.getEntity(entityId);
        }
        else {
            entity = entityRef;
        }
        if (entity != null) {
            try {
                if (regionName != null) {
                    LOG.debug("Including entity: {}/{}", regionName, entity.getId());
            	    pageContext.getRequest().setAttribute("_region_" + regionName, region);
                } else {
                    LOG.debug("Including entity without region: {}", entity.getId());
                    pageContext.getRequest().setAttribute("_entity_" + entity.getId(), entity);
                }
                webRequestContext.pushContainerSize(containerSize);
                this.decorateInclude(ControllerUtils.getIncludePath(entity), entity);
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing entity tag", e);
            }
            finally {
                webRequestContext.popContainerSize();
            }
        } else {
            LOG.debug("Entity not found in region: {}/{}", regionName, entityId);
        }

        return SKIP_BODY;
    }
}
