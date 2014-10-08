package com.sdl.tridion.referenceimpl.webapp.controller.core;

import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.Region;
import com.sdl.tridion.referenceimpl.webapp.ViewAttributeNames;
import com.sdl.tridion.referenceimpl.webapp.controller.exception.BadRequestException;
import com.sdl.tridion.referenceimpl.webapp.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public abstract class ControllerBase {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerBase.class);

    protected Page getPageFromRequest(HttpServletRequest request) {
        final Page page = (Page) request.getAttribute(ViewAttributeNames.PAGE_MODEL);
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
}
