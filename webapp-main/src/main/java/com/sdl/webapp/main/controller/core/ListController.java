package com.sdl.webapp.main.controller.core;

import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.main.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.main.RequestAttributeNames.ENTITY_MODEL;
import static com.sdl.webapp.main.controller.ControllerUtils.INCLUDE_PATH_PREFIX;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.*;

@Controller
@RequestMapping(INCLUDE_PATH_PREFIX + CORE_AREA_NAME + "/" + LIST_CONTROLLER_NAME)
public class ListController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(ListController.class);

    @RequestMapping(method = RequestMethod.GET, value = LIST_ACTION_NAME + "/{regionName}/{entityId}")
    public String handleGetList(HttpServletRequest request, @PathVariable String regionName,
                                @PathVariable String entityId) {
        LOG.trace("handleGetList: regionName={}, entityId={}", regionName, entityId);

        final Entity entity = getEntityFromRequest(request, regionName, entityId);
        request.setAttribute(ENTITY_MODEL, entity);

        // TODO: TSI-524 Implement this method

        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);
        return mvcData.getAreaName() + "/Entity/" + mvcData.getViewName();
    }
}
