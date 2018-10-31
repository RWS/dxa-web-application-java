package com.sdl.webapp.common.controller;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.entity.DynamicList;
import com.sdl.webapp.common.controller.exception.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_MAPPING;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * List controller for the Core area that handles include requests to <code>/system/mvc/Framework/List/{regionName}/{entityId}</code>.
 *
 * @see ControllerUtils
 * @dxa.publicApi
 */
@Slf4j
@Controller
@RequestMapping(INCLUDE_MAPPING + "/List")
public class ListController extends EntityController {

    private final WebRequestContext webRequestContext;

    private final ContentProvider contentProvider;

    @Autowired
    public ListController(WebRequestContext webRequestContext, ContentProvider contentProvider) {
        this.webRequestContext = webRequestContext;
        this.contentProvider = contentProvider;
    }

    private static int getOrDefault(HttpServletRequest request, String parameterName, int defaultValue) {
        final String parameter = request.getParameter(parameterName);
        return !Strings.isNullOrEmpty(parameter) ? Integer.parseInt(parameter) : defaultValue;
    }

    /**
     * Handles a request to fill a dynamic list with data.
     *
     * @param request  current request
     * @param entityId entity id
     * @return the name of the entity view that should be rendered for this request.
     * @throws java.lang.Exception exception in case view is not resolved for any reason
     */
    @RequestMapping(value = "List" + "/{entityId}")
    public String handleGetList(HttpServletRequest request, @PathVariable String entityId) throws Exception {
        log.trace("handleGetList: entityId={}", entityId);
        // The List action is effectively just an alias for the general Entity action (we keep it for backward compatibility).
        return handleEntityRequest(request, entityId);
    }

    @Override
    protected ViewModel enrichModel(ViewModel model, HttpServletRequest request) throws Exception {
        if (model instanceof DynamicList) {
            log.trace("Model {} is a list entity, processing");

            final ViewModel enrichedEntity = super.enrichModel(model, request);
            final DynamicList dynamicList = enrichedEntity instanceof EntityModel ? (DynamicList) enrichedEntity : (DynamicList) model;

            if (!isEmpty(dynamicList.getQueryResults())) {
                log.debug("Dynamic list {}is already populated with results, returning model {}", dynamicList, model);
                return model;
            }

            // we only take the start from the query string
            // if there is also an id parameter matching the model entity id,
            // means that we are sure that the paging is coming from the right entity (if there is more than one paged list on the page)
            if (Objects.equals(dynamicList.getId(), request.getParameter("id"))) {
                //we need to run a query to populate the list
                dynamicList.setStart(getOrDefault(request, "start", 0));
            }

            try {
                contentProvider.populateDynamicList(dynamicList, webRequestContext.getLocalization());
            } catch (ContentProviderException e) {
                log.error("An unexpected error occurred", e);
                throw new InternalServerErrorException("An unexpected error occurred", e);
            }
        }
        return model;
    }
}
