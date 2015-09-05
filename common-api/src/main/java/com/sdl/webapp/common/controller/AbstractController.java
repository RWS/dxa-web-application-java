package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.controller.exception.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;

/**
 * Abstract superclass for controllers with utility methods and exception handling.
 */
public abstract class AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

    @Autowired
    protected ViewResolver viewResolver;

    protected PageModel getPageFromRequest(HttpServletRequest request) {
        final PageModel page = (PageModel) request.getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.error("Page not found in request attributes");
            throw new NotFoundException();
        }
        return page;
    }

    protected RegionModel getIncludePageFromRequest(HttpServletRequest request, String includePageName) {
        final PageModel page = getPageFromRequest(request);
        final RegionModel includePage = page.getRegions().get(includePageName);
        if (includePage == null) {
            LOG.error("Include page not found on page: {}", includePageName);
            throw new NotFoundException("Include page not found on page: " + includePageName);
        }
        return includePage;
    }

    protected RegionModel getRegionFromRequest(HttpServletRequest request, String regionName) {
        final PageModel page = getPageFromRequest(request);
        //final RegionModelSet regionSet = (RegionModelSet) request.getAttribute(REGIONSET_MODEL);
        RegionModel region = page.getRegions().get(regionName);
        /*if(regionSet != null)
        {
        	region = regionSet.get(regionName);
        }*/
        
        if (region == null) {

            // Check if the region is active on the request
            //
            region = (RegionModel) request.getAttribute("_region_" + regionName);

            if ( region == null ) {
                LOG.error("Region not found on page: {}", regionName);
                throw new NotFoundException("Region not found on page: " + regionName);
            }
        }
        return region;
    }

    protected EntityModel getEntityFromRequest(HttpServletRequest request, String regionName, String entityId) {
        final RegionModel region = getRegionFromRequest(request, regionName);
        final EntityModel entity = region.getEntity(entityId); // region.getEntities().get(entityId);
        if (entity == null) {
            LOG.error("Entity not found in region: {}/{}", regionName, entityId);
            throw new NotFoundException("Entity not found in region: " + regionName + "/" + entityId);
        }
        return entity;
    }

    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception exception) {
        LOG.error("Exception while processing request for: {}", request.getRequestURL(), exception);
        return ControllerUtils.SECTION_ERROR_VIEW;
    }

    protected String resolveView(MvcData mvcData, String type, HttpServletRequest request) {
        return this.viewResolver.resolveView(mvcData, type, request);
    }

    protected String resolveView(String viewBaseDir, String view, MvcData mvcData, HttpServletRequest request) {
        return this.viewResolver.resolveView(viewBaseDir, view, mvcData, request);
    }

}
