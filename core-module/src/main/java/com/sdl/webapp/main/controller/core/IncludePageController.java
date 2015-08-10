package com.sdl.webapp.main.controller.core;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;
import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_PATH_PREFIX;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.CORE_AREA_NAME;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.PAGE_ACTION_NAME;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.PAGE_CONTROLLER_NAME;

/**
 * Include page controller for the Core area.
 *
 * This handles include requests to /system/mvc/Core/Page/{includePageName}
 */
@Controller
@RequestMapping(INCLUDE_PATH_PREFIX + CORE_AREA_NAME + "/" + PAGE_CONTROLLER_NAME)
public class IncludePageController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(IncludePageController.class);

    /**
     * Handles a request for an include page.
     *
     * @param request The request.
     * @param includePageName The name of the include page.
     * @param viewName The name of the view to use (optional; overrides the name of the view in the include page
     *                 if specified).
     * @return The name of the page view that should be rendered for this request.
     */
    @RequestMapping(method = RequestMethod.GET, value = PAGE_ACTION_NAME + "/{includePageName}")
    public String handleGetIncludePage(HttpServletRequest request, @PathVariable String includePageName,
                                       @RequestParam(required = false) String viewName) {
        LOG.trace("handleGetIncludePage: includePageName={}", includePageName);

        final Page includePage = getIncludePageFromRequest(request, includePageName);
        request.setAttribute(PAGE_MODEL, includePage);

        final MvcData mvcData = includePage.getMvcData();
        LOG.trace("Include Page MvcData: {}", mvcData);

        // If view name not specified in request, use view name from page model
        if (Strings.isNullOrEmpty(viewName)) {
            return resolveView(mvcData, "Page", request);
            //return mvcData.getAreaName() + "/Page/" + mvcData.getViewName();
        }

        String[] viewParts = viewName.split("/");
        return resolveView(viewParts[0], viewParts[1], mvcData, request);

        //return viewName;
    }
}
