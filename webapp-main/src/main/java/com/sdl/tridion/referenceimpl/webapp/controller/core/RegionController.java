package com.sdl.tridion.referenceimpl.webapp.controller.core;

import com.sdl.tridion.referenceimpl.common.model.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.tridion.referenceimpl.webapp.WebAppConstants.REGION_MODEL;
import static com.sdl.tridion.referenceimpl.webapp.WebAppConstants.REGION_PATH_PREFIX;

@Controller
@RequestMapping(REGION_PATH_PREFIX)
public class RegionController extends ControllerBase {
    private static final Logger LOG = LoggerFactory.getLogger(RegionController.class);

    @RequestMapping(method = RequestMethod.GET, value = "{regionName}")
    public String handleGetRegion(HttpServletRequest request, @PathVariable String regionName) {
        LOG.debug("handleGetRegion: regionName={}", regionName);

        final Region region = getRegionFromRequest(request, regionName);

        request.setAttribute(REGION_MODEL, region);

        return region.getViewName();
    }
}
