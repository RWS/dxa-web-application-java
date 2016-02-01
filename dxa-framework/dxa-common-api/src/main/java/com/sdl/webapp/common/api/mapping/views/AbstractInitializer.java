package com.sdl.webapp.common.api.mapping.views;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class AbstractInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractInitializer.class);

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @PostConstruct
    private void initialize() {
        if (getClass().isAnnotationPresent(RegisteredView.class)) {
            LOG.debug("AutoRegistering (@RegisteredView present) view for module {}", getAreaName());
            registerViewEntry(getClass().getAnnotation(RegisteredView.class));
        }

        if (getClass().isAnnotationPresent(RegisteredViews.class)) {
            LOG.debug("AutoRegistering (@RegisteredViews present) views for module {}", getAreaName());
            final RegisteredViews views = getClass().getAnnotation(RegisteredViews.class);
            for (RegisteredView viewEntry : views.value()) {
                registerViewEntry(viewEntry);
            }
        }
    }

    protected abstract String getAreaName();

    private void registerViewEntry(RegisteredView viewEntry) {
        LOG.debug("View {} for class {}", viewEntry.viewName(), viewEntry.clazz());
        registerViewModel(viewEntry.viewName(), viewEntry.clazz(), viewEntry.controllerName());
    }

    private void registerViewModel(String viewName, Class<? extends ViewModel> entityClass, String controllerName) {
        if (Strings.isNullOrEmpty(controllerName)) {
            LOG.debug("controllerName is empty, trying to register view {} with entity class {}", viewName, entityClass);
            if (EntityModel.class.isAssignableFrom(entityClass)) {
                controllerName = "Entity";
            } else if (RegionModel.class.isAssignableFrom(entityClass)) {
                controllerName = "Region";
            } else if (PageModel.class.isAssignableFrom(entityClass)) {
                controllerName = "Page";
            } else {
                LOG.error("Couldn't register view {} because entityClass {} is not of a known type.", viewName, entityClass);
                return;
            }
        }

        MvcData mvcData = MvcDataImpl.builder()
                .areaName(getAreaName())
                .viewName(viewName)
                .controllerName(controllerName)
                .build();

        LOG.debug("Registering MvcData {} for entity class {}", mvcData, entityClass);

        viewModelRegistry.registerViewModel(mvcData, entityClass);
    }
}
