package com.sdl.webapp.main.controller.core;

import com.sdl.webapp.common.api.content.ContentProviderException;
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
import static com.sdl.webapp.main.controller.ControllerUtils.REQUEST_PATH_PREFIX;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.CORE_AREA_NAME;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.ENTITY_ACTION_NAME;
import static com.sdl.webapp.main.controller.core.CoreAreaConstants.ENTITY_CONTROLLER_NAME;

@Controller
@RequestMapping(REQUEST_PATH_PREFIX + CORE_AREA_NAME + "/" + ENTITY_CONTROLLER_NAME)
public class EntityController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(EntityController.class);

    @RequestMapping(method = RequestMethod.GET, value = ENTITY_ACTION_NAME + "/{regionName}/{entityId}")
    public String handleGetEntity(HttpServletRequest request, @PathVariable String regionName,
                                  @PathVariable String entityId)
            throws ContentProviderException {
        LOG.trace("handleGetEntity: regionName={}, entityId={}", regionName, entityId);

        final Entity entity = getEntityFromRequest(request, regionName, entityId);
        request.setAttribute(ENTITY_MODEL, entity);

        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);
        return mvcData.getAreaName() + "/Entity/" + mvcData.getViewName();
    }
}
