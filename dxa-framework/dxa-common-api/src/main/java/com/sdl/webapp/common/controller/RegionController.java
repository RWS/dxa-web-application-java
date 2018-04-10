package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_MAPPING;
import static com.sdl.webapp.common.controller.RequestAttributeNames.REGION_MODEL;

/**
 * Region controller for the Core area.
 * <p>
 * This handles include requests to /system/mvc/Framework/Region/{regionName}
 * </p>
 *
 * @see ControllerUtils
 * @dxa.publicApi
 */
@Controller
@RequestMapping(INCLUDE_MAPPING + "/Region")
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
    @RequestMapping(value = "Region" + "/{regionName}")
    public String handleGetRegion(HttpServletRequest request, @PathVariable String regionName) throws Exception {
        LOG.trace("handleGetRegion: regionName={}", regionName);


        final RegionModel originalModel = getRegionFromRequest(request, regionName);
        final ViewModel enrichedModel = enrichModel(originalModel, request);
        final RegionModel region = enrichedModel instanceof RegionModel ? (RegionModel) enrichedModel : originalModel;

        request.setAttribute(REGION_MODEL, region);

        final MvcData mvcData = region.getMvcData();
        LOG.trace("Region MvcData: {}", mvcData);
        return viewNameResolver.resolveView(mvcData, "Region");
    }
}
