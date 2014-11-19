package com.sdl.webapp.main.controller;

import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.main.controller.exception.BadRequestException;
import com.sdl.webapp.main.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.main.RequestAttributeNames.PAGE_MODEL;

public abstract class AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

    protected static final String ERROR_VIEW = "Shared/Error";

    protected Page getPageFromRequest(HttpServletRequest request) {
        final Page page = (Page) request.getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.error("Page not found in request attributes");
            throw new BadRequestException("Page not found in request attributes");
        }
        return page;
    }

    protected Page getIncludePageFromRequest(HttpServletRequest request, String includePageName) {
        final Page page = getPageFromRequest(request);
        final Page includePage = page.getIncludes().get(includePageName);
        if (includePage == null) {
            LOG.error("Include page not found on page: {}", includePageName);
            throw new NotFoundException("Include page not found on page: " + includePageName);
        }
        return includePage;
    }

    protected Region getRegionFromRequest(HttpServletRequest request, String regionName) {
        final Page page = getPageFromRequest(request);
        final Region region = page.getRegions().get(regionName);
        if (region == null) {
            LOG.error("Region not found on page: {}", regionName);
            throw new NotFoundException("Region not found on page: " + regionName);
        }
        return region;
    }

    protected Entity getEntityFromRequest(HttpServletRequest request, String regionName, String entityId) {
        final Region region = getRegionFromRequest(request, regionName);
        final Entity entity = region.getEntities().get(entityId);
        if (entity == null) {
            LOG.error("Entity not found in region: {}/{}", regionName, entityId);
            throw new NotFoundException("Entity not found in region: " + regionName + "/" + entityId);
        }
        return entity;
    }

    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception exception) {
        LOG.error("Exception while processing request for: {}", request.getRequestURL(), exception);
        return ERROR_VIEW;
    }
}
