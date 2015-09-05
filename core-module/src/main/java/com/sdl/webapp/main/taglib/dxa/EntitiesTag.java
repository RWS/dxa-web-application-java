package com.sdl.webapp.main.taglib.dxa;

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

public class EntitiesTag extends AbstractMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(EntitiesTag.class);

    private String regionName;
    private RegionModel parentRegion;
    private int containerSize;
    
    public void setRegion(String regionName) {
        this.regionName = regionName;
    }

    public void setParentRegion(RegionModel parent)
    {
    	this.parentRegion = parent;
    }

    public void setContainerSize(int containerSize)
    {
    	this.containerSize = containerSize;
    }
    
    @Override
    public int doStartTag() throws JspException {
        final PageModel page = (PageModel) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        RegionModel region = null;
        if(parentRegion != null)
        {
        	region = parentRegion;	
        }
        else
        {
        	region = page.getRegions().get(regionName);
        }
        
        if (region == null) {
            LOG.debug("Region not found on page: {}", regionName);
            return SKIP_BODY;
        }

        WebRequestContext webRequestContext = this.getWebRequestContext();
        for (EntityModel entity : region.getEntities().values()) {
            try {
            	pageContext.getRequest().setAttribute("_region_" + regionName, region);
                webRequestContext.pushContainerSize(containerSize);
            	this.decorateInclude(ControllerUtils.getIncludePath(entity), entity);
                //pageContext.include(ControllerUtils.getIncludePath(entity));
            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing entity tag", e);
            }
            finally {
                webRequestContext.popContainerSize();
            }
        }

        return SKIP_BODY;
    }
}
