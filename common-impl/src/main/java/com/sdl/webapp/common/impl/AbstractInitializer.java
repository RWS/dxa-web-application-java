package com.sdl.webapp.common.impl;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.model.*;
import com.sdl.webapp.common.impl.mapping.MvcDataImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 13/10/2015.
 */
public abstract class AbstractInitializer {

    private final ViewModelRegistry viewModelRegistry;

    private final String areaName;


    public AbstractInitializer(ViewModelRegistry viewModelRegistry, String areaName) {
        this.viewModelRegistry = viewModelRegistry;
        this.areaName = areaName;
    }

    protected void registerViewModel(String viewName,  Class<? extends ViewModel> entityClass)
    {
        registerViewModel(viewName, entityClass, null);
    }
    protected void registerViewModel(String viewName,  Class<? extends ViewModel> entityClass, String controllerName)
    {
        if (Strings.isNullOrEmpty(controllerName))
        {
            if(EntityModel.class.isAssignableFrom(entityClass))
            {
                controllerName = "Entity";
            }
            else if (RegionModel.class.isAssignableFrom(entityClass))
            {
                controllerName = "Region";
            }
            else
            {
                controllerName = "Page";
            }
        }

        MvcDataImpl mvcData = new MvcDataImpl();
        mvcData.setAreaName(this.areaName);
        mvcData.setViewName(viewName);
        mvcData.setControllerName(controllerName);

        viewModelRegistry.registerViewModel(mvcData, entityClass);
    }
}
