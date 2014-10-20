package com.sdl.webapp.main.controller.core;

import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.main.controller.exception.BadRequestException;
import com.sdl.webapp.main.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.main.WebAppConstants.PAGE_MODEL;

public abstract class ControllerBase {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerBase.class);

    protected static final String ERROR_VIEW = "shared/Error";

    protected Page getPageFromRequest(HttpServletRequest request) {
        final Page page = (Page) request.getAttribute(PAGE_MODEL);
        if (page == null) {
            LOG.error("Page not found in request attributes");
            throw new BadRequestException("Page not found in request attributes");
        }
        return page;
    }

    protected Region getRegionFromRequest(HttpServletRequest request, String regionName) {
        final Page page = getPageFromRequest(request);
        final Region region = page.getRegions().get(regionName);
        if (region == null) {
            LOG.error("Region not found: {}", regionName);
            throw new NotFoundException("Region not found: " + regionName);
        }
        return region;
    }

    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception exception) {
        LOG.error("Exception while processing request for: {}", request.getRequestURL(), exception);
        return ERROR_VIEW;
    }
}
