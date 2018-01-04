package com.sdl.webapp.common.controller;

import com.sdl.dxa.mvc.ViewNameResolver;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.entity.ExceptionEntity;
import com.sdl.webapp.common.controller.exception.NotFoundException;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.getDefaultActionName;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.getDefaultControllerAreaName;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.getDefaultControllerName;

/**
 * Abstract superclass for controllers with utility methods and exception handling.
 * @dxa.publicApi
 */
@Controller
@Slf4j
public abstract class BaseController {

    @Autowired
    protected ViewNameResolver viewNameResolver;

    @Autowired
    protected WebRequestContext context;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private static Boolean isCustomAction(MvcData mvcData) {
        return !Objects.equals(mvcData.getActionName(), getDefaultActionName())
                || !Objects.equals(mvcData.getControllerName(), getDefaultControllerName())
                || !Objects.equals(mvcData.getControllerAreaName(), getDefaultControllerAreaName());
    }

    RegionModel getRegionFromRequest(HttpServletRequest request, String regionName) {
        RegionModel region = (RegionModel) request.getAttribute("_region_");
        if (region == null) {
            log.error("Region not found on page: {}", regionName);
            throw new NotFoundException("Region not found on page: " + regionName);
        }
        return region;
    }

    EntityModel getEntityFromRequest(HttpServletRequest request, String entityId) {
        final EntityModel entity = (EntityModel) request.getAttribute("_entity_");
        if (entity == null) {
            log.error("Entity not found in request: {}", entityId);
            throw new NotFoundException("Entity not found in request: " + entityId);
        }
        return entity;
    }

    @RequestMapping(value = ControllerUtils.INCLUDE_PATH_PREFIX + ControllerUtils.SECTION_ERROR_VIEW)
    public String handleJspIncludesErrors() {
        log.error("Unhandled exception from JSP include action");

        return ControllerUtils.SECTION_ERROR_VIEW;
    }

    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception exception) {
        log.error("Exception while processing request for: {}", request.getRequestURL(), exception);

        return ControllerUtils.SECTION_ERROR_VIEW;
    }

    /**
     * This is the method to override if you need to add custom model population logic,
     * first calling the base class and then adding your own logic
     *
     * @param model              The model which you wish to add data to.
     * @param httpServletRequest http servlet request for current request, can be null
     * @return A fully populated view model combining CMS content with other data
     * @throws java.lang.Exception exception
     */
    protected ViewModel enrichModel(ViewModel model, HttpServletRequest httpServletRequest) throws Exception {

        if (modelBindingRequired(model, httpServletRequest)) {
            log.trace("Model data [model id:{} <> request] binding is required", model);
            ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(model);
            dataBinder.bind(httpServletRequest);
            Validator validator = dataBindValidator();
            if (validator != null) {
                dataBinder.setValidator(validator);
                dataBinder.validate();
                httpServletRequest.setAttribute("dataBinding", dataBinder.getBindingResult());
            }
        }

        return model;
    }

    /**
     * Returns whether controller should attempt to bind data from request to model. Override if you need a data binding.
     * If you want to have data binding, you may also want to validate it afterwards. Check {@link #dataBindValidator()}.
     *
     * @return to bind or not to bind
     */
    protected boolean modelBindingRequired(ViewModel model, HttpServletRequest httpServletRequest) {
        return false;
    }

    /**
     * Returns a Spring {@link Validator} that may be used to validate {@link ViewModel} after data binding. Is called
     * only if model binding is required {@link #modelBindingRequired(ViewModel, HttpServletRequest)}.
     * <p>If this method returns {@code null}, there will be no validation obviously.</p>
     * <p>The validation result (if this method returns {@code !null}) is set as request attribute with a key {@code dataBinding}.</p>
     *
     * @return a validator to use during data binding, or null to not to use validation
     */
    @Nullable
    protected Validator dataBindValidator() {
        return null;
    }

    /**
     * Enriches a given Entity Model using an appropriate (custom) Controller.
     * <p>
     * This method is different from EnrichModel in that it doesn't expect the current Controller to be able to enrich the Entity Model;
     * it creates a Controller associated with the Entity Model for that purpose.
     * </p>
     * It is used by PageController.enrichEmbeddedModels.
     *
     * @param entity  The Entity Model to enrich.
     * @param request request
     * @return The enriched Entity Model.
     */
    protected EntityModel enrichEntityModel(EntityModel entity, HttpServletRequest request) {
        if (entity == null || entity.getMvcData() == null || !isCustomAction(entity.getMvcData())) {
            return entity;
        }

        MvcData mvcData = entity.getMvcData();

        String controllerName = mvcData.getControllerName() != null ? mvcData.getControllerName() : getDefaultControllerName();
        String controllerAreaName = mvcData.getControllerAreaName() != null ? mvcData.getControllerAreaName() : getDefaultControllerAreaName();

        Map<RequestMappingInfo, HandlerMethod> handlerMethods =
                this.requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> item : handlerMethods.entrySet()) {
            RequestMappingInfo mapping = item.getKey();

            for (String urlPattern : mapping.getPatternsCondition().getPatterns()) {
                if (urlPattern.contains('/' + controllerAreaName + '/' + controllerName)) {
                    HandlerMethod controllerMethod = handlerMethods.get(mapping);
                    BaseController controller = (BaseController) ApplicationContextHolder.getContext().getBean(controllerMethod.getBean().toString());
                    try {
                        controller.enrichModel(entity, request);
                        return entity;
                    } catch (Exception e) {
                        log.error("Error in EnrichModel", e);
                        return new ExceptionEntity(e); // TODO: What about MvcData?
                    }
                }
            }
        }
        return entity;
    }
}
