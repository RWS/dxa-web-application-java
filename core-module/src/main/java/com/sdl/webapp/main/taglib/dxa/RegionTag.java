package com.sdl.webapp.main.taglib.dxa;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.SimpleRegionMvcData;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.AbstractMarkupTag;
import com.sdl.webapp.common.controller.ControllerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import java.io.IOException;

import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;

public class RegionTag extends AbstractMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(RegionTag.class);

    private String name;
    private boolean placeholder;
    private RegionModel parentRegion;
    private int containerSize;
    
    public void setName(String name) {
        this.name = name;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
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
        if(Strings.isNullOrEmpty(name) || page.getMvcData().getViewName().equals("IncludePage")){
        	//special case where we wish to render an include page as region
        	this.pageContext.setAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE, "1");
        	   // Create a new Region Model which reflects the Page Model
            name = page.getName().replace(" ", "-");
            MvcData mvcData = new SimpleRegionMvcData(name);

            RegionModelImpl includeregion = null;
            try {
                includeregion = new RegionModelImpl(name);
            } catch (DxaException e) {
                e.printStackTrace();
            }
            includeregion.setMvcData(mvcData);
            includeregion.setRegions(page.getRegions());
            region = includeregion;
        }
        else
        {
        	region = page.getRegions().get(name);
        }
        if(parentRegion != null)
        {
        	region = parentRegion.getRegions().get(name);
        }
        
        if ( region == null && placeholder == true ) {
            // Render the region even if it is not present on the page, so XPM region markup etc can be generated
            //

            RegionModelImpl placeholderRegion = null;
            try {
                placeholderRegion = new RegionModelImpl(name);
            } catch (DxaException e) {
                e.printStackTrace();
            }
            placeholderRegion.setMvcData(new SimpleRegionMvcData(name));
            region = placeholderRegion;
            pageContext.getRequest().setAttribute("_region_" + name, placeholderRegion);
        }

        if (region != null) {
            LOG.debug("Including region: {}", name);
            try {

                //pageContext.include(ControllerUtils.getIncludePath(region));
            	
            	pageContext.getRequest().setAttribute("_region_" + name, region);
            	pageContext.getRequest().setAttribute("_containersize_" + name, containerSize);
            	
                this.decorateInclude(ControllerUtils.getIncludePath(region), region);

            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing region tag: " + name, e);
            }
        } else {
            LOG.debug("Region not found on page: {}", name);
        }
        
        return SKIP_BODY;
    }

}
