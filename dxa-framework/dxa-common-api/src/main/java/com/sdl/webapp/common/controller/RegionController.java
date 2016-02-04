package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_PATH_PREFIX;
import static com.sdl.webapp.common.controller.RequestAttributeNames.REGION_MODEL;

/**
 * Region controller for the Core area.
 * <p>
 * This handles include requests to /system/mvc/Core/Region/{regionName}
 * </p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@Controller
@RequestMapping(INCLUDE_PATH_PREFIX + DefaultsMvcData.CoreAreaConstants.CORE_AREA_NAME + '/' + DefaultsMvcData.CoreAreaConstants.REGION_CONTROLLER_NAME)
public class RegionController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(RegionController.class);

    /**
     * Handles a request for a region.
     *
     * @param request    The request.
     * @param regionName The region name.
     * @return The name of the region view that should be rendered for this request.
     * @throws java.lang.Exception exception
     */
    @RequestMapping(method = RequestMethod.GET, value = DefaultsMvcData.CoreAreaConstants.REGION_ACTION_NAME + "/{regionName}")
    public String handleGetRegion(HttpServletRequest request, @PathVariable String regionName) throws Exception {
        LOG.trace("handleGetRegion: regionName={}", regionName);


        final RegionModel originalModel = getRegionFromRequest(request, regionName);
        final ViewModel enrichedModel = enrichModel(originalModel, request);
        final RegionModel region = enrichedModel instanceof RegionModel ? (RegionModel) enrichedModel : originalModel;

        request.setAttribute(REGION_MODEL, region);

        final MvcData mvcData = region.getMvcData();
        LOG.trace("Region MvcData: {}", mvcData);
        return resolveView(mvcData, "Region", request);
    }


}
