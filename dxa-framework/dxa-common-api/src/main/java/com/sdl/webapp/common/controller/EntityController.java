package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.CORE_AREA_NAME;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CoreAreaConstants.ENTITY_CONTROLLER_NAME;
import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_PATH_PREFIX;
import static com.sdl.webapp.common.controller.RequestAttributeNames.ENTITY_MODEL;

/**
 * Entity controller for the Core area that handles include requests to <code>/system/mvc/Core/Entity/Entity/{entityId}</code>.
 */
@Controller
@RequestMapping(INCLUDE_PATH_PREFIX + CORE_AREA_NAME + '/' + ENTITY_CONTROLLER_NAME)
public class EntityController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(EntityController.class);


    /**
     * Handles a request for an entity.
     *
     * @param request  The request.
     * @param entityId The entity id.
     * @param map      the model map to deal with forms
     * @return The name of the entity view that should be rendered for this request.
     * @throws ContentProviderException If an error occurs so that the entity cannot not be retrieved.
     * @throws java.lang.Exception      if any.
     */
    @RequestMapping(value = DefaultsMvcData.CoreAreaConstants.ENTITY_ACTION_NAME + "/{entityId}")
    public String handleGetEntity(HttpServletRequest request,
                                  @PathVariable String entityId, ModelMap map)
            throws Exception {
        return handleEntityRequest(request, entityId, map);
    }

    protected String handleEntityRequest(HttpServletRequest request, String entityId) throws Exception {
        return handleEntityRequest(request, entityId, null);
    }

    protected String handleEntityRequest(HttpServletRequest request, String entityId, ModelMap map) throws Exception {
        LOG.trace("handleGetEntity: entityId={}", entityId);

        final EntityModel originalModel = getEntityFromRequest(request, entityId);
        final ViewModel enrichedEntity = enrichModel(originalModel, request);
        final EntityModel entity = enrichedEntity instanceof EntityModel ? (EntityModel) enrichedEntity : originalModel;

        request.setAttribute(ENTITY_MODEL, entity);

        final MvcData mvcData = entity.getMvcData();
        LOG.trace("Entity MvcData: {}", mvcData);

        return viewNameResolver.resolveView(mvcData, "Entity");
    }
}
