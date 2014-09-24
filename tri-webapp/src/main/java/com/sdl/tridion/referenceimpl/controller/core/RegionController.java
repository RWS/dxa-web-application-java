package com.sdl.tridion.referenceimpl.controller.core;

import com.sdl.tridion.referenceimpl.common.model.Region;
import com.sdl.tridion.referenceimpl.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class RegionController {
    private static final Logger LOG = LoggerFactory.getLogger(RegionController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/region")
    public String handleGetRegion(HttpServletRequest request) {
        final Region region = getRegion(request);
        LOG.debug("handleGetRegion: region={}", region);

        return region.getViewName();
    }

    private Region getRegion(HttpServletRequest request) {
        final Region region = (Region) request.getAttribute(JspBeanNames.REGION_MODEL);
        if (region == null) {
            LOG.error("Region not found in request attributes");
            throw new NotFoundException("Region not found in request attributes");
        }

        return region;
    }
}
