package com.sdl.dxa.modules.test.model;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.ApplicationContextHolder;

public class CustomRegionModelImpl extends RegionModelImpl {


    private ContentProvider contentProvider = ApplicationContextHolder.getContext().getBean(ContentProvider.class);

    private WebRequestContext webRequestContext = ApplicationContextHolder.getContext().getBean(WebRequestContext.class);

    public CustomRegionModelImpl(String name) throws DxaException {
        super(name);
        try {
            this.addEntity(contentProvider.getEntityModel("17249-17331", webRequestContext.getLocalization()));
        } catch (ContentProviderException e) {
            e.printStackTrace();
        }
    }

    public CustomRegionModelImpl(String name, String qualifiedViewName) throws DxaException {
        super(name, qualifiedViewName);
    }

    @Override
    public String getXpmMarkup(Localization localization) {
        return "<!-- custom XPM Markup Here -->";
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof CustomRegionModelImpl)
        {
            return ((CustomRegionModelImpl) obj).getName().equals(this.getName());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.getName().hashCode();
    }
}
