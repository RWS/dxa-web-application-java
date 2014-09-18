package com.sdl.tridion.referenceimpl.controller;

import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.Region;
import com.sdl.tridion.referenceimpl.controller.exception.BadRequestException;
import com.sdl.tridion.referenceimpl.controller.exception.ForbiddenException;
import com.sdl.tridion.referenceimpl.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class RegionController {
    private static final Logger LOG = LoggerFactory.getLogger(RegionController.class);

    private static final String REGION_VIEW_PREFIX = "region/";

    @RequestMapping(method = RequestMethod.GET, value = "/region/{name}")
    public String handleGetRegion(HttpServletRequest request, @PathVariable("name") String name) {
        LOG.debug("handleGetRegion: name={}", name);

        final Page pageModel = (Page) request.getAttribute(JspBeanNames.PAGE_MODEL);
        if (pageModel == null) {
            LOG.warn("Access to region without page model: {}", name);
            throw new ForbiddenException("Access to region without page model");
        }

        final Region regionModel = pageModel.getRegion(name);
        if (regionModel == null) {
            LOG.warn("Region not found: {} for page: {}", name, pageModel.getId());
            throw new NotFoundException("Region not found: " + name + " for page: " + pageModel.getId());
        }

        request.setAttribute(JspBeanNames.REGION_MODEL, regionModel);

        return REGION_VIEW_PREFIX + regionModel.getViewName();
    }
}
