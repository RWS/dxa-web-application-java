package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.common.controller.RequestAttributeNames.REGION_MODEL;
import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_PATH_PREFIX;

/**
 * Region controller for the Core area.
 *
 * This handles include requests to /system/mvc/Core/Region/{regionName}
 */
@Controller
@RequestMapping(INCLUDE_PATH_PREFIX + CoreAreaConstants.CORE_AREA_NAME + "/" + CoreAreaConstants.REGION_CONTROLLER_NAME)
public class RegionController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(RegionController.class);

     /**
     * Handles a request for a region.
     *
     * @param request The request.
     * @param regionName The region name.
     * @return The name of the region view that should be rendered for this request.
     */
    @RequestMapping(method = RequestMethod.GET, value = CoreAreaConstants.REGION_ACTION_NAME + "/{regionName}")
    public String handleGetRegion(HttpServletRequest request, @PathVariable String regionName ) throws Exception {
        LOG.trace("handleGetRegion: regionName={}", regionName);


        final RegionModel originalModel = getRegionFromRequest(request, regionName);
        final ViewModel enrichedModel = EnrichModel(originalModel);
        final RegionModel region = enrichedModel instanceof RegionModel ? (RegionModel)enrichedModel:originalModel;

        request.setAttribute(REGION_MODEL, region);

        final MvcData mvcData = region.getMvcData();
        LOG.trace("Region MvcData: {}", mvcData);
        return resolveView(mvcData, "Region", request);
        //return mvcData.getAreaName() + "/Region/" + mvcData.getViewName();
    }


}
