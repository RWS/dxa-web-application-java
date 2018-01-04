package com.sdl.webapp.common.api.mapping.views;

import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * AbstractModuleInitializer which initializes views registration in modules with {@link RegisteredViewModel} and {@link RegisteredViewModels}.
 *
 * @dxa.publicApi
 */
@Slf4j
public abstract class AbstractModuleInitializer {

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @PostConstruct
    private void initialize() {
        log.debug("Registration for views in {}", getClass().getCanonicalName());

        //todo dxa2 make ModuleInfo mandatory
        if (getClass().isAnnotationPresent(ModuleInfo.class)) {
            ModuleInfo moduleInfo = getClass().getAnnotation(ModuleInfo.class);
            log.trace("Found module moduleInfo annotation {} for module {}", moduleInfo, moduleInfo.name());
            //todo dxa2 skip() should also skip Spring initialization
            if (moduleInfo.skip()) {
                log.info("Skipping module {} initialization because moduleInfo says skip=true", moduleInfo.name());
                return;
            }
            log.debug("Initialization for module {} with moduleInfo: {}", moduleInfo.name(), moduleInfo.description());
        }

        registerViews();
    }

    private void registerViews() {
        if (getClass().isAnnotationPresent(RegisteredViewModel.class)) {
            traceAutoRegistering(RegisteredViewModel.class);
            registerViewEntry(getClass().getAnnotation(RegisteredViewModel.class));
        }

        if (getClass().isAnnotationPresent(RegisteredViewModels.class)) {
            traceAutoRegistering(RegisteredViewModels.class);
            final RegisteredViewModels views = getClass().getAnnotation(RegisteredViewModels.class);
            for (RegisteredViewModel viewEntry : views.value()) {
                registerViewEntry(viewEntry);
            }
        }
    }

    private void traceAutoRegistering(Class<? extends Annotation> annotation) {
        log.debug("AutoRegistering ({} present) view for module {}", annotation, getAreaName());
    }

    /**
     * <p>Returns the name of module/area.</p>
     *
     * @return a folder name where views are stored .
     */
    //todo dxa2 remove in preference of @ModuleInfo
    protected abstract String getAreaName();

    private void registerViewEntry(RegisteredViewModel viewEntry) {
        log.debug("View {} for class {}", viewEntry.viewName(), viewEntry.modelClass());
        registerViewModel(viewEntry.viewName(), viewEntry.modelClass(), viewEntry.controllerName());
    }

    private void registerViewModel(String viewName, Class<? extends ViewModel> entityClass, String controllerName) {
        String actualControllerName = getControllerNameForEntityClass(controllerName, entityClass);
        boolean shouldHaveControllerName = shouldHaveControllerName(entityClass);

        if (!shouldHaveControllerName && actualControllerName != null) {
            log.warn("Controller name is not expected for {} but is {}", entityClass, controllerName);
        }

        log.debug("controllerName {} for view {} with entity class {}", actualControllerName, viewName, entityClass);

        MvcData mvcData = isNullOrEmpty(viewName) || isNullOrEmpty(actualControllerName) ? null : MvcDataImpl.newBuilder()
                .areaName(getAreaName())
                .viewName(viewName)
                .controllerName(actualControllerName)
                .build();

        log.debug("Registering MvcData {} for entity class {}", mvcData, entityClass);

        viewModelRegistry.registerViewModel(mvcData, entityClass);
    }

    private String getControllerNameForEntityClass(String controllerName, Class<? extends ViewModel> entityClass) {
        if (!isNullOrEmpty(controllerName)) {
            return controllerName;
        }

        log.debug("controllerName is empty, trying to get controller name for entity class {}", entityClass);

        if (EntityModel.class.isAssignableFrom(entityClass)) {
            return "Entity";
        }
        if (RegionModel.class.isAssignableFrom(entityClass)) {
            return "Region";
        }
        if (PageModel.class.isAssignableFrom(entityClass)) {
            return "Page";
        }
        return null;
    }

    private boolean shouldHaveControllerName(Class<? extends ViewModel> entityClass) {
        return EntityModel.class.isAssignableFrom(entityClass) ||
                RegionModel.class.isAssignableFrom(entityClass) ||
                PageModel.class.isAssignableFrom(entityClass);
    }
}
