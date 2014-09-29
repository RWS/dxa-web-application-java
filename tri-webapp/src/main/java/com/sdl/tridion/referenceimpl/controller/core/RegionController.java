package com.sdl.tridion.referenceimpl.controller.core;

import com.sdl.tridion.referenceimpl.common.model.Region;
import com.sdl.tridion.referenceimpl.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class RegionController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(RegionController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/region/{name}")
    public String handleGetRegion(HttpServletRequest request, @PathVariable("name") String name) {
        LOG.debug("handleGetRegion: name={}", name);

        final Region region = getPageFromRequest(request).getRegions().get(name);
        if (region == null) {
            LOG.error("Region not found: {}", name);
            throw new NotFoundException("Region not found: " + name);
        }

        request.setAttribute(ViewAttributeNames.REGION_MODEL, region);

        return region.getViewName();
    }
}
