package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.api.WebRequestContext;
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
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import java.io.IOException;

import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;

public class RegionTag extends AbstractMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(RegionTag.class);

    private String name;
    private boolean placeholder;
    private String emptyviewname;
    private int containerSize;
    
    public void setName(String name) {
        this.name = name;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }

    public void setEmptyviewname(String emptyviewname) {
        this.emptyviewname = emptyviewname;
    }

    public void setContainerSize(int containerSize) {
        this.containerSize = containerSize;
    }
    
    @Override
    public int doStartTag() throws JspException {
        WebRequestContext webRequestContext = this.getWebRequestContext();

        RegionModel parentRegion = webRequestContext.getParentRegion();

        final PageModel page = (PageModel) pageContext.getRequest().getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        RegionModel region;
        if (StringUtils.isEmpty(name)) {
            //special case where we wish to render an include page as region
            this.pageContext.setAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE, "1");
            // Create a new Region Model which reflects the Page Model
            String regionName = page.getName().replace(" ", "-");
            MvcData mvcData = new SimpleRegionMvcData(regionName);

            RegionModelImpl includeRegion = null;
            try {
                includeRegion = new RegionModelImpl(regionName);
                includeRegion.setMvcData(mvcData);
                includeRegion.setRegions(page.getRegions());
            } catch (DxaException e) {
                LOG.error(String.format("Exception when creating new regionModel %s", name), e);
                e.printStackTrace();
            }

            region = includeRegion;
        } else {
            region = page.getRegions().get(name);
        }

        if(parentRegion != null) {
            region = parentRegion.getRegions().get(name);
        }
        
        if (region == null && placeholder) {
            // Render the region even if it is not present on the page, so XPM region markup etc can be generated

            RegionModelImpl placeholderRegion = null;
            try {
                placeholderRegion = new RegionModelImpl(name);
                SimpleRegionMvcData mvcData = null;
                if (StringUtils.isEmpty(emptyviewname)) {
                    mvcData = new SimpleRegionMvcData(name);
                } else {
                    mvcData = new SimpleRegionMvcData(name, emptyviewname);
                }

                placeholderRegion.setMvcData(mvcData);
            } catch (DxaException e) {
                LOG.error(String.format("Exception when creating new placeholderRegion %s", name), e);
                e.printStackTrace();
            }


            region = placeholderRegion;
            pageContext.getRequest().setAttribute("_region_" + name, placeholderRegion);
        }

        if (region != null) {
            String regionName = region.getName();
            LOG.debug("Including region: {}", regionName);
            
            try {
            	pageContext.getRequest().setAttribute("_region_" + regionName, region);
                webRequestContext.pushParentRegion(region);
                webRequestContext.pushContainerSize(containerSize);

                this.decorateInclude(ControllerUtils.getIncludePath(region), region);

            } catch (ServletException | IOException e) {
                throw new JspException("Error while processing region tag: " + regionName, e);
            }
            finally {
                webRequestContext.popParentRegion();
                webRequestContext.popContainerSize();
            }
        } else {
            LOG.debug("Region not found on page: {}", name);
        }
        
        return SKIP_BODY;
    }
}
