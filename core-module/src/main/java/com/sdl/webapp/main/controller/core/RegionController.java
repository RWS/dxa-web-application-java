package com.sdl.webapp.main.controller.core;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.common.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.common.controller.RequestAttributeNames.REGION_MODEL;
import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_PATH_PREFIX;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.CORE_AREA_NAME;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.REGION_ACTION_NAME;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.REGION_CONTROLLER_NAME;

/**
 * Region controller for the Core area.
 *
 * This handles include requests to /system/mvc/Core/Region/{regionName}
 */
@Controller
@RequestMapping(INCLUDE_PATH_PREFIX + CORE_AREA_NAME + "/" + REGION_CONTROLLER_NAME)
public class RegionController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(RegionController.class);

    /**
     * Handles a request for a region.
     *
     * @param request The request.
     * @param regionName The region name.
     * @return The name of the region view that should be rendered for this request.
     */
    @RequestMapping(method = RequestMethod.GET, value = REGION_ACTION_NAME + "/{regionName}")
    public String handleGetRegion(HttpServletRequest request, @PathVariable String regionName) {
        LOG.trace("handleGetRegion: regionName={}", regionName);

        final Region region = getRegionFromRequest(request, regionName);
        request.setAttribute(REGION_MODEL, region);

        final MvcData mvcData = region.getMvcData();
        LOG.trace("Region MvcData: {}", mvcData);
        return resolveView(mvcData, "Region", request);
        //return mvcData.getAreaName() + "/Region/" + mvcData.getViewName();
    }
}
